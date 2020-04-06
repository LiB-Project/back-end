package br.edu.ifpb.lib.service;

import br.edu.ifpb.lib.domain.AreaBasica;
import br.edu.ifpb.lib.domain.Documento;
import br.edu.ifpb.lib.domain.DocumentoAcessos;
import br.edu.ifpb.lib.domain.SubArea;
import br.edu.ifpb.lib.repository.DocumentoAcessosRepository;
import br.edu.ifpb.lib.repository.DocumentoRepository;
import br.edu.ifpb.lib.web.valueobject.AreaEstatisticaVO;
import br.edu.ifpb.lib.web.valueobject.DocumentoAcessosVO;
import br.edu.ifpb.lib.web.valueobject.LevantamentoVO;
import br.edu.ifpb.lib.web.valueobject.SubAreaQuantidade;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

@Service
@Log4j2
public class EstatisticaService {

    private final DocumentoRepository documentoRepository;
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final GrandeAreaService grandeAreaService;
    private final SubAreaService subAreaService;
    private final AreaBasicaService areaBasicaService;
    private final DocumentoAcessosRepository documentoAcessosRepository;

    public EstatisticaService(DocumentoRepository documentoRepository, ElasticsearchTemplate elasticsearchTemplate, GrandeAreaService grandeAreaService, SubAreaService subAreaService, AreaBasicaService areaBasicaService, DocumentoAcessosRepository documentoAcessosRepository) {
        this.documentoRepository = documentoRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.grandeAreaService = grandeAreaService;
        this.subAreaService = subAreaService;
        this.areaBasicaService = areaBasicaService;
        this.documentoAcessosRepository = documentoAcessosRepository;
    }

    private List<Integer> readAnos(){
        List<Integer> anoList = new ArrayList<>();

        documentoRepository.findAll().forEach(doc -> anoList.add(doc.getAnoPublicacao()));

        return anoList;
    }



    public List<Integer> buscarListaDeAnosDisponiveis(){
        return readAnos().stream().distinct().collect(Collectors.toList());
    }

    public List<LevantamentoVO> fazerLevantamento(int anoInferior, int anoSuperior, String cursoId) {
        List<LevantamentoVO> levantamentoVOList = new ArrayList<>();

        for(int ano = anoInferior; ano <= anoSuperior; ano++) {
            LevantamentoVO levantamentoVO = new LevantamentoVO();
            levantamentoVO.setAno(ano);
            int count;
            if (cursoId.equalsIgnoreCase("Todos")) {
                count = documentoRepository.countAllByAnoPublicacaoEquals(ano);
            } else {
                count = documentoRepository.countAllByCursoIdEqualsAndAnoPublicacaoEquals(cursoId, ano);
            }
            levantamentoVO.setQuantidade(count);
            levantamentoVOList.add(levantamentoVO);
        }

        return levantamentoVOList;
    }

    public List<DocumentoAcessosVO> documentoAcessosList(){
        Iterable<DocumentoAcessos> all = this.documentoAcessosRepository.findAll();
        List<DocumentoAcessosVO> voList = new ArrayList<>();

        all.forEach(entity -> {
            Documento documento = documentoRepository.findById(entity.getId()).get();
            DocumentoAcessosVO build = DocumentoAcessosVO.builder()
                    .id(entity.getId())
                    .titulo(documento.getTitulo())
                    .quantidadeAcessos(entity.getAcessos())
                    .build();
            voList.add(build);
        });
        return voList.stream()
                .limit(10)
                .sorted((o1, o2) -> Long.compare(o2.getQuantidadeAcessos(), o1.getQuantidadeAcessos()))
                .collect(Collectors.toList());
    }

    public void countNovoAcesso(String idDocument){
        Optional<DocumentoAcessos> byId = documentoAcessosRepository.findById(idDocument);
        DocumentoAcessos acessos = null;

        if(byId.isPresent()){
            acessos = byId.get();
        }else{
            acessos = new DocumentoAcessos();
            acessos.setId(idDocument);
        }

        acessos.setAcessos(acessos.getAcessos() + 1);
        documentoAcessosRepository.save(acessos);
    }

    public List<AreaEstatisticaVO> buscarEstatisticaDeGrandeArea(Long codigoGrandeArea) {
        List<AreaEstatisticaVO> areaEstatisticaVOList = new ArrayList<>();

        List<AreaBasica> areaBasicaList = areaBasicaService.listarTodas()
                .parallelStream().filter(a -> a.getGrandeArea().equals(codigoGrandeArea)).collect(Collectors.toList());
        List<SubArea> subAreas = subAreaService.listarTodas();

        areaBasicaList.parallelStream().forEach(area -> {
            List<SubAreaQuantidade> quantidadeList = new ArrayList<>();
            List<SubArea> filter = subAreas.parallelStream().filter(sub -> sub.getAreaBasica().equals(area.getCodigo())).collect(Collectors.toList());
            filter.parallelStream().forEach(sub -> {
                SearchQuery searchQuery = new NativeSearchQueryBuilder()
                        .withIndices("documento")
                        .withQuery(matchQuery("subAreasId", sub.getCodigo()))
                        .build();
                int count = elasticsearchTemplate.queryForList(searchQuery, Documento.class).size();
                if(count > 0) quantidadeList.add(new SubAreaQuantidade(sub, count));
            });

            if(quantidadeList.size() > 0)
                areaEstatisticaVOList.add(new AreaEstatisticaVO(area.getNome(), quantidadeList));
        });

        return areaEstatisticaVOList;
    }

    public List<AreaEstatisticaVO> buscarEstatisticaDeTodasGrandesAreas() {
        List<AreaEstatisticaVO> areaEstatisticaVOList = new ArrayList<>();

        List<AreaBasica> areaBasicaList = areaBasicaService.listarTodas();
        List<SubArea> subAreas = subAreaService.listarTodas();

        areaBasicaList.parallelStream().forEach(area -> {
            List<SubAreaQuantidade> quantidadeList = new ArrayList<>();
            List<SubArea> filter = subAreas.parallelStream().filter(sub -> sub.getAreaBasica().equals(area.getCodigo())).collect(Collectors.toList());
            filter.parallelStream().forEach(sub -> {
                SearchQuery searchQuery = new NativeSearchQueryBuilder()
                        .withIndices("documento")
                        .withQuery(matchQuery("subAreasId", sub.getCodigo()))
                        .build();
                int count = elasticsearchTemplate.queryForList(searchQuery, Documento.class).size();
                if(count > 0) quantidadeList.add(new SubAreaQuantidade(sub, count));
            });

            if(quantidadeList.size() > 0)
                areaEstatisticaVOList.add(new AreaEstatisticaVO(area.getNome(), quantidadeList));
        });

        return areaEstatisticaVOList;
    }
}
