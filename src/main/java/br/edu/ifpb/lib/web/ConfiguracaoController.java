package br.edu.ifpb.lib.web;

import br.edu.ifpb.lib.service.ConfiguracaoService;
import br.edu.ifpb.lib.service.exceptions.EntidadeNaoEncontradaException;
import br.edu.ifpb.lib.domain.Configuracao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/api/configuracao"
)
public class ConfiguracaoController {

    private final ConfiguracaoService configuracaoService;

    public ConfiguracaoController(ConfiguracaoService configuracaoService) {
        this.configuracaoService = configuracaoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_USER')")
    public ResponseEntity<Configuracao> cadastrarConfiguracao(@RequestBody Configuracao config){
        Configuracao save = configuracaoService.salvar(config);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(save);
    }

    @GetMapping
    public ResponseEntity<Configuracao> recuperarConfiguracao(){
        try {
            return ResponseEntity.ok(configuracaoService.recuperarConfiguracao());
        } catch (EntidadeNaoEncontradaException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header("errorMessage", e.getMessage())
                    .body(null);
        }
    }

    @PostMapping("/restaurar")
    public ResponseEntity<Void> restaurarConfiguracao() {
        configuracaoService.restaurar();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tituloSistema")
    public ResponseEntity<String> recuperarTituloSistema(){
        try {
            return ResponseEntity.ok(configuracaoService.recuperarConfiguracao().getTituloSistema());
        } catch (EntidadeNaoEncontradaException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header("errorMessage", e.getMessage())
                    .body(null);
        }
    }

    @GetMapping("/faviconBase64")
    public ResponseEntity<String> recuperarFaviconSistema(){
        try {
            return ResponseEntity.ok(configuracaoService.recuperarConfiguracao().getFaviconBase64());
        } catch (EntidadeNaoEncontradaException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header("errorMessage", e.getMessage())
                    .body(null);
        }
    }

    @GetMapping("/logomarcaBase64")
    public ResponseEntity<String> recuperarLogomarcaSistema(){
        try {
            return ResponseEntity.ok(configuracaoService.recuperarConfiguracao().getLogomarcaBase64());
        } catch (EntidadeNaoEncontradaException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header("errorMessage", e.getMessage())
                    .body(null);
        }
    }

    @GetMapping("/iconeBase64")
    public ResponseEntity<String> recuperarIconeSistema(){
        try {
            return ResponseEntity.ok(configuracaoService.recuperarConfiguracao().getFaviconBase64());
        } catch (EntidadeNaoEncontradaException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header("errorMessage", e.getMessage())
                    .body(null);
        }
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('SUPER_USER')")
    public ResponseEntity<Configuracao> atualizarConfiguracao(@PathVariable String id, @RequestBody Configuracao config){
        config.setId(id);
        Configuracao configuracao = configuracaoService.atualizar(config);
        return ResponseEntity.ok(configuracao);
    }
}
