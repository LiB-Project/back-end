package br.edu.ifpb.lib.web;

import br.edu.ifpb.lib.RecomendacaoResponse;
import br.edu.ifpb.lib.service.grpc.RecomendacaoServiceClient;
import br.edu.ifpb.lib.web.valueobject.RecomendacaoVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(
        value="/api/recomendacao"
)
public class RecomendacaoController {

    private final RecomendacaoServiceClient recomendacaoServiceClient;

    public RecomendacaoController(RecomendacaoServiceClient recomendacaoServiceClient) {
        this.recomendacaoServiceClient = recomendacaoServiceClient;
    }

    @GetMapping
    public ResponseEntity<List<RecomendacaoVO>> listarRecomendacao(@RequestParam String document){
        List<RecomendacaoVO> recomendacaoVOS = recomendacaoServiceClient.buscarRecomendacoes(document);
        return ResponseEntity.ok(recomendacaoVOS);
    }

}
