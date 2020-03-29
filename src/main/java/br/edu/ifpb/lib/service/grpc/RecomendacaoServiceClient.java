package br.edu.ifpb.lib.service.grpc;

import br.edu.ifpb.lib.RecomendacaoGrpc;
import br.edu.ifpb.lib.RecomendacaoRequest;
import br.edu.ifpb.lib.RecomendacaoResponse;
import br.edu.ifpb.lib.domain.Documento;
import br.edu.ifpb.lib.repository.DocumentoRepository;
import br.edu.ifpb.lib.web.valueobject.DocumentoVO;
import br.edu.ifpb.lib.web.valueobject.RecomendacaoVO;
import com.google.protobuf.Empty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RecomendacaoServiceClient {
    private final RecomendacaoGrpc.RecomendacaoBlockingStub stub;
    private final DocumentoRepository documentoRepository;

    public RecomendacaoServiceClient(RecomendacaoGrpc.RecomendacaoBlockingStub stub, DocumentoRepository documentoRepository) {
        this.stub = stub;
        this.documentoRepository = documentoRepository;
    }

    public List<RecomendacaoVO> buscarRecomendacoes(String idDocument){
        RecomendacaoRequest request = RecomendacaoRequest.newBuilder()
                .setIdDocument(idDocument)
                .build();

        RecomendacaoResponse recomendacaoResponse = stub.buscarRecomendacoes(request);

        List<RecomendacaoVO> recomendacaoList = recomendacaoResponse.getRecomendacoesList().parallelStream().map(item -> {
            Optional<Documento> doc = documentoRepository.findById(item.getIdDocument());
            if (doc.isPresent()) {
                return new RecomendacaoVO(doc.get().getId(), doc.get().getTitulo(), item.getSimilaridade());
            }
            return null;
        }).sorted(Comparator.comparingDouble((RecomendacaoVO recomendacaoVO1) -> recomendacaoVO1 != null ? recomendacaoVO1.getSimilaridade() : null).reversed())
                .collect(Collectors.toList());

        recomendacaoList.removeIf(recomendacaoVO -> recomendacaoVO.getId().equals(idDocument) || recomendacaoVO.getSimilaridade() == 0);
        return recomendacaoList;
    }

    public void removerDocumentoDaRecomendacao(String idDocument){
        RecomendacaoRequest request = RecomendacaoRequest.newBuilder()
                .setIdDocument(idDocument)
                .build();
        stub.removerDocumentoDoTreinamento(request);
        log.info(String.format("Documento=%s removido do mecanismo de recomendação", idDocument));
    }
}
