package br.edu.ifpb.lib.service;

import br.edu.ifpb.lib.domain.AreaBasica;
import br.edu.ifpb.lib.domain.Documento;
import br.edu.ifpb.lib.domain.DocumentoAcessos;
import br.edu.ifpb.lib.domain.SubArea;
import br.edu.ifpb.lib.repository.DocumentoAcessosRepository;
import br.edu.ifpb.lib.repository.DocumentoRepository;
import br.edu.ifpb.lib.web.valueobject.*;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.index.query.Operator;
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
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

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

    public EvolucaoVO fazerLevantamento(int anoInferior, int anoSuperior, String cursoId) {
        EvolucaoVO evolucaoVO = new EvolucaoVO();

        Set<Documento> documentAllList = new HashSet<>();

        List<LevantamentoVO> levantamentoVOList = new ArrayList<>();
        for(int ano = anoInferior; ano <= anoSuperior; ano++) {
            LevantamentoVO levantamentoVO = new LevantamentoVO();
            levantamentoVO.setAno(ano);
            List<Documento> documentoList;
            if (cursoId.equalsIgnoreCase("Todos")) {
                documentoList = documentoRepository.findAllByAnoPublicacaoEquals(ano);
            } else {
                documentoList = documentoRepository.findAllByCursoIdEqualsAndAnoPublicacaoEquals(cursoId, ano);
            }
            levantamentoVO.setQuantidade(documentoList.size());
            levantamentoVOList.add(levantamentoVO);

            documentAllList.addAll(documentoList);
        }

        List<AreaEstatisticaVO> areaEstatisticaVOList =
                this.generateAreaEstatisticasByDocumentList(new ArrayList<>(documentAllList));

        evolucaoVO.setLevantamentoList(levantamentoVOList);
        evolucaoVO.setAreaEstatisticaList(areaEstatisticaVOList);

        return evolucaoVO;
    }

    private List<AreaEstatisticaVO> generateAreaEstatisticasByDocumentList(List<Documento> documentList) {
        Set<Long> subAreaSet = new HashSet<>();
        documentList.stream().forEach(doc -> subAreaSet.addAll(doc.getSubAreasId()));

        Set<Long> areaBasicaSet = new HashSet<>();

        List<SubAreaQuantidade> subAreaQuantidadeList = new ArrayList<>();
        subAreaSet.stream().forEach(subAreaId -> {
            SubArea subArea = subAreaService.buscarPorCodigo(subAreaId).get();
            Long count = documentList.stream().filter(doc -> doc.getSubAreasId().contains(subAreaId)).count();
            if(count > 0) {
                areaBasicaSet.add(subArea.getAreaBasica());
                subAreaQuantidadeList.add(new SubAreaQuantidade(subArea, Integer.valueOf(count.toString())));
            }
        });

        return areaBasicaSet.stream().map(area -> {
            String nomeArea = areaBasicaService.buscarPorCodigo(area).get().getNome();
            List<SubAreaQuantidade> subAreaQuantidades = subAreaQuantidadeList.stream()
                    .filter(subAreaQuantidade -> subAreaQuantidade.getSubArea().getAreaBasica().equals(area))
                    .collect(Collectors.toList());
            return new AreaEstatisticaVO(nomeArea, subAreaQuantidades);
        }).collect(Collectors.toList());
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

    public List<AreaEstatisticaVO> buscarEstatisticaPorOrientador(String idOrientador) {
        SearchQuery searchQueryOrientador = new NativeSearchQueryBuilder()
                .withIndices("documento")
                .withQuery(multiMatchQuery(idOrientador, "orientadorId","coorientadorId"))
                .build();
        List<Documento> documentosList = elasticsearchTemplate.queryForList(searchQueryOrientador, Documento.class);

        Set<Long> subAreaSet = new HashSet<>();
        documentosList.stream().forEach(doc -> subAreaSet.addAll(doc.getSubAreasId()));

        Set<Long> areaBasicaSet = new HashSet<>();

        List<SubAreaQuantidade> subAreaQuantidadeList = new ArrayList<>();
        subAreaSet.stream().forEach(subAreaId -> {
            SubArea subArea = subAreaService.buscarPorCodigo(subAreaId).get();
            Long count = documentosList.stream().filter(doc -> doc.getSubAreasId().contains(subAreaId)).count();
            if(count > 0) {
                areaBasicaSet.add(subArea.getAreaBasica());
                subAreaQuantidadeList.add(new SubAreaQuantidade(subArea, Integer.valueOf(count.toString())));
            }
        });

        return areaBasicaSet.stream().map(area -> {
            String nomeArea = areaBasicaService.buscarPorCodigo(area).get().getNome();
            List<SubAreaQuantidade> subAreaQuantidades = subAreaQuantidadeList.stream()
                    .filter(subAreaQuantidade -> subAreaQuantidade.getSubArea().getAreaBasica().equals(area))
                    .collect(Collectors.toList());
            return new AreaEstatisticaVO(nomeArea, subAreaQuantidades);
        }).collect(Collectors.toList());
    }
}
