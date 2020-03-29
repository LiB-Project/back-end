package br.edu.ifpb.lib.service;

import br.edu.ifpb.lib.domain.AreaBasica;
import br.edu.ifpb.lib.domain.Documento;
import br.edu.ifpb.lib.domain.SubArea;
import br.edu.ifpb.lib.repository.DocumentoRepository;
import br.edu.ifpb.lib.web.valueobject.AreaEstatisticaVO;
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

    public EstatisticaService(DocumentoRepository documentoRepository, ElasticsearchTemplate elasticsearchTemplate, GrandeAreaService grandeAreaService, SubAreaService subAreaService, AreaBasicaService areaBasicaService) {
        this.documentoRepository = documentoRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.grandeAreaService = grandeAreaService;
        this.subAreaService = subAreaService;
        this.areaBasicaService = areaBasicaService;
    }

    public int buscarAnoInferiorDeDocumentos(){
        Sort sortDataPublicacao = Sort.by(Sort.Direction.ASC, "anoPublicacao");
        PageRequest pageable = PageRequest.of(0, 1, sortDataPublicacao);
        Page<Documento> all = documentoRepository.findAll(pageable);
        Documento documento = all.getContent().get(0);
        if(documento != null)
            return documento.getAnoPublicacao();
        else
            return Year.now().getValue();
    }

    public int buscarAnoSuperiorDeDocumentos(){
        Sort sortDataPublicacao = Sort.by(Sort.Direction.DESC, "anoPublicacao");
        PageRequest pageable = PageRequest.of(0, 1, sortDataPublicacao);
        Page<Documento> all = documentoRepository.findAll(pageable);
        Documento documento = all.getContent().get(0);
        if(documento != null)
            return documento.getAnoPublicacao();
        else
            return Year.now().getValue();
    }

    public List<Integer> buscarListaDeAnosDisponiveis(){
        List<Integer> anoList = new ArrayList<>();
        int inferior = buscarAnoInferiorDeDocumentos();
        int superior = buscarAnoSuperiorDeDocumentos();

        for(int ano = inferior; ano <= superior; ano++){
            anoList.add(ano);
        }

            return anoList;
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
