package br.edu.ifpb.lib.service;

import br.edu.ifpb.lib.service.grpc.LdaServiceClient;
import br.edu.ifpb.lib.service.grpc.RecomendacaoServiceClient;
import br.edu.ifpb.lib.web.valueobject.PublicacaoRecenteVO;
import br.edu.ifpb.lib.web.valueobject.QueryVO;
import lombok.extern.log4j.Log4j2;
import br.edu.ifpb.lib.config.DiretoryConstantsConfig;
import br.edu.ifpb.lib.domain.Documento;
import br.edu.ifpb.lib.repository.CursoRepository;
import br.edu.ifpb.lib.repository.DocumentoRepository;
import br.edu.ifpb.lib.repository.OrientadorRepository;
import br.edu.ifpb.lib.service.exceptions.EntidadeNaoEncontradaException;
import br.edu.ifpb.lib.service.exceptions.ErroAoLerTextoException;
import br.edu.ifpb.lib.service.exceptions.ErroAoSalvarArquivoException;
import br.edu.ifpb.lib.service.grpc.AnalyzerClient;
import br.edu.ifpb.lib.web.valueobject.DocumentoVO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.index.query.functionscore.WeightBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final OrientadorRepository orientadorRepository;
    private final OrientadorService orientadorService;
    private final SubAreaService subAreaService;
    private final CursoService cursoService;
    private final CursoRepository cursoRepository;
    private final DiretoryConstantsConfig diretoryConstantsConfig;
    private final AnalyzerClient analyzerClient;
    private final LdaServiceClient ldaService;
    private final RecomendacaoServiceClient recomendacaoServiceClient;

    public DocumentoService(DocumentoRepository documentoRepository, OrientadorRepository orientadorRepository, OrientadorService orientadorService, SubAreaService subAreaService, CursoService cursoService, CursoRepository cursoRepository, DiretoryConstantsConfig diretoryConstantsConfig, AnalyzerClient analyzerClient, LdaServiceClient ldaService, RecomendacaoServiceClient recomendacaoServiceClient) {
        this.documentoRepository = documentoRepository;
        this.orientadorRepository = orientadorRepository;
        this.orientadorService = orientadorService;
        this.subAreaService = subAreaService;
        this.cursoService = cursoService;
        this.cursoRepository = cursoRepository;
        this.diretoryConstantsConfig = diretoryConstantsConfig;
        this.analyzerClient = analyzerClient;
        this.ldaService = ldaService;
        this.recomendacaoServiceClient = recomendacaoServiceClient;
    }

    public DocumentoVO cadastrarNovo(DocumentoVO documento, MultipartFile arquivo) throws ErroAoLerTextoException, ErroAoSalvarArquivoException, FileNotFoundException {
        Documento entity = toEntity(documento);
        LocalDateTime now = LocalDateTime.now();
        entity.setDataPublicacao(now);
        try {
            // lendo conteúdo textual do documento
            PDDocument load = PDDocument.load(arquivo.getBytes());
            String text = new PDFTextStripper().getText(load);
            load.close();
            entity.setConteudo(text);
        } catch (IOException e) {
            throw new ErroAoLerTextoException(String.format("Não foi possível extrair o conteúdo textual do documento"));
        }
        //salvando arquivo
        String path = diretoryConstantsConfig.getDocumentsDirectory() + File.separator + documento.getTitulo().trim().replaceAll("[\\W]|_", "");
        File file = new File(path);
        try {
            arquivo.transferTo(file);
        } catch (IOException e) {
            throw new ErroAoSalvarArquivoException("Ocorreu um problema ao salvar o arquivo enviado!");
        }
        entity.setPathArquivo(path);
        Documento save = documentoRepository.save(entity);
        analyzerClient.executeFrequencyDistribution(save.getId(), file);
        ldaService.treinarModeloLDA(save);
        return toValue(save);
    }

    public DocumentoVO atualizar(DocumentoVO atualizado){
        Documento documento = toEntity(atualizado);
        Documento save = documentoRepository.save(documento);
        return toValue(save);
    }

    public List<DocumentoVO> listarTodosSemPaginacao(){
        List<Documento> list = new ArrayList<>();
        documentoRepository.findAll().forEach(list::add);
        List<DocumentoVO> collect = list.stream().map(entity -> toValue(entity)).collect(Collectors.toList());
        return collect;
    }

    public boolean documentoExiste(String id){
        return documentoRepository.existsById(id);
    }

    public void deletarDocumento(String id){
        Documento documento = documentoRepository.findById(id).get();
        String pathArquivo = documento.getPathArquivo();
        File file = new File(pathArquivo);
        file.delete();
        documentoRepository.deleteById(id);
        recomendacaoServiceClient.removerDocumentoDaRecomendacao(id);
    }

    public DocumentoVO buscarPorId(String id) throws EntidadeNaoEncontradaException {
        Optional<Documento> byId = documentoRepository.findById(id);
        if(!byId.isPresent())
            throw new EntidadeNaoEncontradaException("Documento não encontrado");
        else
            return toValue(byId.get());
    }

    private Documento toEntity(DocumentoVO vo){
        Documento documento = new Documento();
        documento.setId(vo == null ? null : vo.getId());
        documento.setTitulo(vo.getTitulo());
        documento.setAutor(vo.getAutor());
        documento.setDataApresentacao(vo.getDataApresentacao());
        documento.setOrientadorId(vo.getOrientador().getId());
        documento.setCoorientadorId(vo.getCoorientador() == null ? null : vo.getCoorientador().getId());
        documento.setCursoId(vo.getCurso().getId());
        documento.setIsbn(vo.getIsbn());
        documento.setPalavrasChave(vo.getPalavrasChave());
        documento.setSubAreasId(vo.getSubAreas().stream().map(subArea->subArea.getCodigo()).collect(Collectors.toList()));
        return documento;
    }

    public Page<DocumentoVO> listagemPaginada(Pageable pageable){
        Page<DocumentoVO> page = documentoRepository.findAll(pageable).map(this::toValue);
        return page;
    }

    public Page<DocumentoVO> buscarPorLabel(String label, String termo, Pageable pageable){
        switch (label){
            case "autor":
                MatchQueryBuilder matchQuery = QueryBuilders.matchQuery(label.concat(".nome"), termo);
                return documentoRepository.search(matchQuery, pageable).map(this::toValue);
            case "curso":
                List<String> cursosId = cursoRepository.findAllByNome(termo).stream().map(c -> c.getId()).collect(Collectors.toList());
                return documentoRepository.findAllByCursoIdIn(pageable, cursosId).map(this::toValue);
            case "orientador":
                List<String> orientadorIds = orientadorRepository.findAllByNome(termo).stream().map(o -> o.getId()).collect(Collectors.toList());
                return documentoRepository.findAllByOrientadorIdIn(pageable, orientadorIds).map(this::toValue);
            default:
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(label, termo);
                return documentoRepository.search(matchQueryBuilder, pageable).map(this::toValue);
        }


    }

    private DocumentoVO toValue(Documento entity){
        DocumentoVO vo = new DocumentoVO();
        vo.setId(entity.getId());
        vo.setTitulo(entity.getTitulo());
        vo.setAutor(entity.getAutor());
        vo.setSubAreas(entity.getSubAreasId().stream().map(id->subAreaService.buscarPorCodigo(id).get()).collect(Collectors.toList()));
        vo.setPalavrasChave(entity.getPalavrasChave());
        vo.setCurso(cursoService.buscarPorId(entity.getCursoId()));
        vo.setDataApresentacao(entity.getDataApresentacao());
        vo.setDataPublicacao(entity.getDataPublicacao());
        vo.setIsbn(entity.getIsbn());
        vo.setScore(entity.get_score());

        try {
            vo.setOrientador(orientadorService.buscarPorId(entity.getOrientadorId()));
        } catch (EntidadeNaoEncontradaException e) {
            vo.setOrientador(null);
        }
        try {
            vo.setCoorientador(entity.getCoorientadorId() == null ?
                    null : orientadorService.buscarPorId(entity.getCoorientadorId()));
        } catch (EntidadeNaoEncontradaException e) {
            vo.setCoorientador(null);
            return vo;
        }

        return vo;
    }

    public List<PublicacaoRecenteVO> listarPublicacoesRecentes(int limit){
        List<Documento> list = new ArrayList<>();
        Iterable<Documento> recentes = documentoRepository
                .findAll(Sort.by(Sort.Direction.DESC, "dataPublicacao"));
        recentes.forEach(list::add);
        return list.parallelStream()
                .limit(limit)
                .map(doc -> PublicacaoRecenteVO.builder().id(doc.getId()).titulo(doc.getTitulo()).build())
                .collect(Collectors.toList());
    }

    public void lerEatualizarTodos() {
        this.documentoRepository.findAll()
                .forEach(documentoRepository::save);
    }

    public Page<DocumentoVO> searchQuery(QueryVO query, Pageable pageable) {
        String searchText = query.getSearchText();
        List<FunctionScoreQueryBuilder> functionList = new ArrayList<>();
        query.getQueryRelevanceList()
            .forEach(item -> {
                String field = item.getField().toLowerCase();

                QueryBuilder queryBuilder = QueryBuilders.matchQuery(field, searchText);

                if(!field.equals("conteudo"))
                    queryBuilder = QueryBuilders.matchQuery(field.concat(".nome"), searchText);

                WeightBuilder weightBuilder = ScoreFunctionBuilders.weightFactorFunction(item.getValue().floatValue());
                FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders
                        .functionScoreQuery(queryBuilder, weightBuilder);
                functionList.add(functionScoreQueryBuilder);
            });

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withPageable(pageable);

        functionList.forEach(function -> nativeSearchQueryBuilder.withQuery(function.query()));

        Page<Documento> page = documentoRepository.search(nativeSearchQueryBuilder.build());

        return page.map(this::toValue);
    }
//    byte[] bytes = new byte[0];
//        try {
//        bytes = Files.readAllBytes(new File(entity.getPathArquivo()).toPath());
//        vo.setArquivo(bytes);
//    } catch (IOException e) {
//        throw new FileNotFoundException("Não foi possível encontrar o arquivo do documento");
//    }
}
