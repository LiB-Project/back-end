package br.edu.ifpb.lib.web;

import br.edu.ifpb.lib.domain.NivelCurso;
import br.edu.ifpb.lib.service.CursoService;
import br.edu.ifpb.lib.web.util.PaginationUtil;
import br.edu.ifpb.lib.web.valueobject.CursoVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        value = "/api/curso"
)
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<CursoVO> cadastrarCurso(@RequestBody CursoVO curso){
        CursoVO save = cursoService.cadastrar(curso);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(save);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<CursoVO> atualizarCurso(@PathVariable String id, @RequestBody CursoVO atualizado){
        boolean existe = cursoService.cursoExiste(id);
        if(!existe){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header("errorMessage", "Este curso não existe!")
                    .build();
        }else{
            CursoVO atualizar = cursoService.atualizar(atualizado);
            return ResponseEntity.ok(atualizar);
        }
    }

    @DeleteMapping("/{cursoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<Void> deletarCurso(@PathVariable String cursoId){
        cursoService.deletar(cursoId);
        return ResponseEntity.ok(null);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    public ResponseEntity<List<CursoVO>> listar(Pageable pageable){
        Page<CursoVO> page = cursoService.listaPaginada(pageable);
        HttpHeaders httpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, "/api/curso");
        return new ResponseEntity<>(page.getContent(), httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CursoVO>> listar(){
        return ResponseEntity.ok(cursoService.listar());
    }

    @GetMapping("nivel")
    public ResponseEntity<List<CursoVO>> listarPorNivel(@Param("q") NivelCurso nivel) {
        return ResponseEntity.ok(cursoService.listarPorNivel(nivel));
    }
}
