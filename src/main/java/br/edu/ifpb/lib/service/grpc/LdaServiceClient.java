package br.edu.ifpb.lib.service.grpc;

import br.edu.ifpb.lib.DocumentoConteudo;
import br.edu.ifpb.lib.LdaGrpc;
import br.edu.ifpb.lib.domain.Documento;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class LdaServiceClient {
    private final LdaGrpc.LdaStub ldaServiceStub;

    public LdaServiceClient(LdaGrpc.LdaStub ldaServiceStub) {
        this.ldaServiceStub = ldaServiceStub;
    }

    public void treinarModeloComDocumentos(Iterable<Documento> documentoIterable){
        StreamObserver<Empty> responseObserver = new StreamObserver<Empty>() {
            @Override
            public void onNext(Empty empty) {
                log.info("Processou um documento");
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                log.info("Todos os documentos foram processados");
            }
        };

        StreamObserver<DocumentoConteudo> requestObserver = this.ldaServiceStub.treinarModeloLDAComDocumentos(responseObserver);

        documentoIterable.forEach(documento -> {
            String conteudo = documento.getConteudo() == null ? "" : documento.getConteudo();
            DocumentoConteudo nextValue = DocumentoConteudo.newBuilder().setIdDocumento(documento.getId())
                    .setConteudo(conteudo).build();
            requestObserver.onNext(nextValue);
        });

        requestObserver.onCompleted();
    }

    public void treinarModeloLDA(Documento documento){
        String conteudo = documento.getConteudo() == null ? "" : documento.getConteudo();
        DocumentoConteudo request = DocumentoConteudo.newBuilder().setIdDocumento(documento.getId())
                .setConteudo(conteudo).build();

        StreamObserver<Empty> responseObserver = new StreamObserver<Empty>() {
            @Override
            public void onNext(Empty empty) {
                log.info("Processou o documento");
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                log.info("Todos os documentos foram processados");
            }
        };

        ldaServiceStub.treinarModeloLDA(request, responseObserver);
    }
}
