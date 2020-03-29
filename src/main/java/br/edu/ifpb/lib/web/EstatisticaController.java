package br.edu.ifpb.lib.web;

import br.edu.ifpb.lib.service.EstatisticaService;
import br.edu.ifpb.lib.web.valueobject.AreaEstatisticaVO;
import br.edu.ifpb.lib.web.valueobject.LevantamentoVO;
import br.edu.ifpb.lib.web.valueobject.SubAreaQuantidade;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Response;
import java.util.List;

@Log4j2
@RestController
@RequestMapping(value = "/api/estatistica")
public class EstatisticaController {

    private final EstatisticaService estatisticaService;

    public EstatisticaController(EstatisticaService estatisticaService) {
        this.estatisticaService = estatisticaService;
    }

    @GetMapping("/ano")
    public ResponseEntity<List<Integer>> buscarAnoMaximoOuMinimoDosDocumentos() {
        return ResponseEntity.ok(estatisticaService.buscarListaDeAnosDisponiveis());
    }

    @GetMapping("/levantamento")
    public ResponseEntity<List<LevantamentoVO>> fazerLevantamentoDeDocumentos(@Param("anoInferior") int anoInferior,
                                                                              @Param("anoSuperior") int anoSuperior,
                                                                              @Param("cursoId") String cursoId) {
        return ResponseEntity.ok(estatisticaService.fazerLevantamento(anoInferior, anoSuperior, cursoId));
    }

    @GetMapping("area")
    public ResponseEntity<List<AreaEstatisticaVO>> getEstatisticaDeArea(@Param("grandeArea") Long grandeArea) {
        if(grandeArea == null){
            return ResponseEntity.ok(estatisticaService.buscarEstatisticaDeTodasGrandesAreas());
        }else{
            return ResponseEntity.ok(estatisticaService.buscarEstatisticaDeGrandeArea(grandeArea));
        }
    }
}
