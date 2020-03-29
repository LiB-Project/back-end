package br.edu.ifpb.lib.web;

import br.edu.ifpb.lib.service.AreaBasicaService;
import br.edu.ifpb.lib.service.GrandeAreaService;
import br.edu.ifpb.lib.service.SubAreaService;
import br.edu.ifpb.lib.domain.AreaBasica;
import br.edu.ifpb.lib.domain.GrandeArea;
import br.edu.ifpb.lib.domain.SubArea;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AreasController {

    private final GrandeAreaService grandeAreaService;
    private final AreaBasicaService areaBasicaService;
    private final SubAreaService subAreaService;

    public AreasController(GrandeAreaService grandeAreaService, AreaBasicaService areaBasicaService, SubAreaService subAreaService) {
        this.grandeAreaService = grandeAreaService;
        this.areaBasicaService = areaBasicaService;
        this.subAreaService = subAreaService;
    }

    @GetMapping("/grandeArea")
    public ResponseEntity<List<GrandeArea>> listarGrandesAreas(){
        return ResponseEntity.ok(grandeAreaService.listarTodas());
    }

    @GetMapping("/grandeArea/{codigo}")
    public ResponseEntity<GrandeArea> buscarGrandeAreaPorId(@PathVariable Long codigo){
        Optional<GrandeArea> grandeArea = grandeAreaService.buscarPorCodigo(codigo);
        if(grandeArea.isPresent())
            return ResponseEntity.ok(grandeArea.get());
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @GetMapping("/areaBasica")
    public ResponseEntity<List<AreaBasica>> listarAreasBasicas(){
        return ResponseEntity.ok(areaBasicaService.listarTodas());
    }

    @GetMapping("/areaBasica/{codigo}")
    public ResponseEntity<AreaBasica> buscarAreaBasicaPorId(@PathVariable Long codigo){
        Optional<AreaBasica> areaBasica = areaBasicaService.buscarPorCodigo(codigo);
        if(areaBasica.isPresent())
            return ResponseEntity.ok(areaBasica.get());
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @GetMapping("/subArea")
    public ResponseEntity<List<SubArea>> listarSubAreas(){
        return ResponseEntity.ok(subAreaService.listarTodas());
    }

    @GetMapping("/subArea/{codigo}")
    public ResponseEntity<SubArea> buscarSubAreaPorId(@PathVariable Long codigo){
        Optional<SubArea> subArea = subAreaService.buscarPorCodigo(codigo);
        if(subArea.isPresent())
            return ResponseEntity.ok(subArea.get());
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

}
