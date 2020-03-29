package br.edu.ifpb.lib.web;

import br.edu.ifpb.lib.service.OrientadorService;
import br.edu.ifpb.lib.service.exceptions.EntidadeExistenteException;
import br.edu.ifpb.lib.domain.Orientador;
import br.edu.ifpb.lib.web.util.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        value="/api/orientador"
)
public class OrientadorController {
    private final OrientadorService orientadorService;

    public OrientadorController(OrientadorService orientadorService) {
        this.orientadorService = orientadorService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<Orientador> cadastrarOrientador(@RequestBody Orientador orientador){
        try {
            Orientador save = orientadorService.cadastrar(orientador);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("successMessage","Orientador cadastrado com sucesso!")
                    .body(save);
        } catch (EntidadeExistenteException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("errorMessage",e.getMessage()).build();
        }

    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<Orientador> atualizarOrientador(@RequestBody Orientador atualizado){
        try {
            Orientador atualizar = orientadorService.atualizar(atualizado);
            return ResponseEntity.status(HttpStatus.OK)
                    .header("successMessage","Orientador cadastrado com sucesso!")
                    .body(atualizar);
        } catch (EntidadeExistenteException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("errorMessage",e.getMessage()).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<Void> deletarOrientador(@PathVariable String id){
        orientadorService.deletar(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<List<Orientador>> listar(){
        List<Orientador> listar = orientadorService.listar();
        return ResponseEntity.ok(listar);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<List<Orientador>> listarComPaginacao(Pageable pageable) {
        Page<Orientador> page = orientadorService.listaPaginada(pageable);
        HttpHeaders httpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, "/api/orientador");
        return new ResponseEntity<>(page.getContent(), httpHeaders, HttpStatus.OK);
    }
}
