package br.edu.ifpb.lib.web;

import br.edu.ifpb.lib.service.AnalysisService;
import br.edu.ifpb.lib.domain.FrequencyDistribution;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {
    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<FrequencyDistribution> findFrequencyByDocument(@PathVariable String documentId){
        return ResponseEntity.ok(analysisService.getFrequencyByDocument(documentId));
    }

}
