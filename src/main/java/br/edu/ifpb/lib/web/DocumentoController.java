package br.edu.ifpb.lib.web;

import br.edu.ifpb.lib.domain.Documento;
import br.edu.ifpb.lib.repository.DocumentoRepository;
import br.edu.ifpb.lib.service.DocumentoService;
import br.edu.ifpb.lib.service.exceptions.EntidadeNaoEncontradaException;
import br.edu.ifpb.lib.service.exceptions.ErroAoLerTextoException;
import br.edu.ifpb.lib.service.exceptions.ErroAoSalvarArquivoException;
import br.edu.ifpb.lib.service.grpc.LdaServiceClient;
import br.edu.ifpb.lib.web.valueobject.PublicacaoRecenteVO;
import br.edu.ifpb.lib.web.valueobject.QueryVO;
import lombok.extern.log4j.Log4j2;
import br.edu.ifpb.lib.web.util.PaginationUtil;
import br.edu.ifpb.lib.web.valueobject.DocumentoVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.List;

@Log4j2
@RestController
@RequestMapping(value = "/api/documento")
public class DocumentoController {
    private final DocumentoService documentoService;
    private final DocumentoRepository documentoRepository;
    private final LdaServiceClient ldaServiceClient;

    public DocumentoController(DocumentoService documentoService, DocumentoRepository documentoRepository, LdaServiceClient ldaServiceClient) {
        this.documentoService = documentoService;
        this.documentoRepository = documentoRepository;
        this.ldaServiceClient = ldaServiceClient;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<DocumentoVO> cadastrarNovo(@RequestPart("metadata") DocumentoVO documento,
                                               @RequestPart("file") MultipartFile file){
        try {
            DocumentoVO documentoVO = documentoService.cadastrarNovo(documento, file);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(documentoVO);
        } catch (ErroAoLerTextoException | ErroAoSalvarArquivoException | FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("errorMessage", e.getMessage())
                    .build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentoVO> buscarPorId(@PathVariable  String id){
        try {
            DocumentoVO documentoVO = documentoService.buscarPorId(id);
            return ResponseEntity.ok(documentoVO);
        } catch (EntidadeNaoEncontradaException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header("errorMessage", e.getMessage()).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<DocumentoVO>> listarComPaginacao(Pageable pageable) {
        Page<DocumentoVO> page = documentoService.listagemPaginada(pageable);
        HttpHeaders httpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, "/api/documento");
        return new ResponseEntity<>(page.getContent(), httpHeaders, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<Void> deletarDocumento(@PathVariable String id){
        documentoService.deletarDocumento(id);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<DocumentoVO> atualizarDocumento(@PathVariable String id, @RequestBody DocumentoVO updated){
        boolean existe = documentoService.documentoExiste(id);
        if(!existe){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .header("errorMessage", "Este documento não existe!")
                            .build();
        }else{
            DocumentoVO update = documentoService.atualizar(updated);
            return ResponseEntity.ok(update);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<DocumentoVO>> buscaPorLabel(@RequestParam("label") String label,
                                                           @RequestParam("termo") String termo,
                                                           Pageable pageable){
        Page<DocumentoVO> page = documentoService.buscarPorLabel(label, termo, pageable);
        HttpHeaders httpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, "/api/documento/search");
        return new ResponseEntity<>(page.getContent(), httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/searchQuery")
    public ResponseEntity<List<DocumentoVO>> searchQuery(@RequestBody QueryVO query, Pageable pageable){
        Page<DocumentoVO> page = documentoService.searchQuery(query, pageable);
        HttpHeaders httpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, "/api/documento/searchQuery");
        return new ResponseEntity<>(page.getContent(), httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<DocumentoVO>> listarTodos(){
        List<DocumentoVO> documentoVOList = documentoService.listarTodosSemPaginacao();
        return ResponseEntity.ok(documentoVOList);
    }

    @GetMapping("/treinar")
    public ResponseEntity<Void> treinarModeloComDocumentos(){
        Iterable<Documento> all = this.documentoRepository.findAll();
        ldaServiceClient.treinarModeloComDocumentos(all);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/treinar/{id}")
    public ResponseEntity<Void> treinarModelo(@PathVariable  String id){
        Documento documento = this.documentoRepository.findById(id).get();
        ldaServiceClient.treinarModeloLDA(documento);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/recentes")
    public ResponseEntity<List<PublicacaoRecenteVO>> listarRecentes(@Param("limit") int limit) {
        return ResponseEntity.ok(this.documentoService.listarPublicacoesRecentes(limit));
    }

    @GetMapping("atualizar")
    public ResponseEntity<Void> lerEAtualizarTodos() {
        documentoService.lerEatualizarTodos();
        return ResponseEntity.ok(null);
    }

}
