package br.edu.ifpb.lib.web;

import br.edu.ifpb.lib.service.UserService;
import br.edu.ifpb.lib.service.exceptions.EntidadeExistenteException;
import br.edu.ifpb.lib.service.exceptions.EntidadeNaoEncontradaException;
import br.edu.ifpb.lib.web.util.PaginationUtil;
import lombok.extern.log4j.Log4j2;
import br.edu.ifpb.lib.domain.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuario")
@Log4j2
public class UsuarioController {
    private final UserService userService;

    public UsuarioController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
//    @PreAuthorize("hasRole('SUPER_USER')")
    public ResponseEntity<Usuario> cadastrarUsuario(@RequestBody Usuario usuario){
        try {
            Usuario save = userService.cadastrar(usuario);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("successMessage","Usuário cadastrado com sucesso!")
                    .body(save);
        } catch (EntidadeExistenteException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("errorMessage", e.getMessage()).build();
        }

    }

    @GetMapping
//    @PreAuthorize("hasRole('SUPER_USER')")
    public ResponseEntity<List<Usuario>> listar(Pageable pageable){
        Page<Usuario> page = userService.listagemPaginada(pageable);
        HttpHeaders httpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, "/api/user");
        return new ResponseEntity<>(page.getContent(), httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/data")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<Usuario> buscarDadosDoUsuarioLogado(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String login = (String) authentication.getPrincipal();
            Usuario usuario = userService.buscarPorLogin(login);
            usuario.setSenha(null);
            return ResponseEntity.ok(usuario);
        } catch (EntidadeNaoEncontradaException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header("errorMessage", e.getMessage()).build();
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('SUPER_USER')")
    public ResponseEntity<Usuario> atualizarUsuario(@RequestBody Usuario atualizado){
        try {
            Usuario atualizar = userService.atualizar(atualizado);
            return ResponseEntity.ok(atualizar);
        } catch (EntidadeExistenteException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("errorMessage", "Este login já está em uso!").build();
        }
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('SUPER_USER')")
    public ResponseEntity<Usuario> buscarUsuario(@PathVariable String id){
        try {
            Usuario usuario = userService.buscarPorId(id);
            return ResponseEntity.ok(usuario);
        } catch (EntidadeNaoEncontradaException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header("errorMessage", "Usuário não encontrado!").build();
        }
    }

    @DeleteMapping("{id}")
//    @PreAuthorize("hasRole('SUPER_USER')")
    public ResponseEntity<Void> deletarUsuario(@PathVariable String id){
        userService.deletarUsuario(id);
        return ResponseEntity.status(HttpStatus.OK)
                .header("successMessage","Usuário removido com sucesso!")
                .body(null);
    }

}
