package br.edu.ifpb.lib.service.grpc;

import br.edu.ifpb.lib.Document;
import br.edu.ifpb.lib.DocumentServiceGrpc;
import br.edu.ifpb.lib.FrequencyDistributionRequest;
import br.edu.ifpb.lib.domain.FrequencyDistribution;
import br.edu.ifpb.lib.service.ConfiguracaoService;
import br.edu.ifpb.lib.service.exceptions.EntidadeNaoEncontradaException;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import br.edu.ifpb.lib.domain.Configuracao;
import br.edu.ifpb.lib.domain.Frequency;
import br.edu.ifpb.lib.repository.FrequencyDistributionRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AnalyzerClient {
    private final DocumentServiceGrpc.DocumentServiceStub stub;
    private final ConfiguracaoService configuracaoService;
    private final AnalyzerHandler handlerRequests;
    private final FrequencyDistributionRepository frequencyRepository;

    public AnalyzerClient(DocumentServiceGrpc.DocumentServiceStub stub, ConfiguracaoService configuracaoService, AnalyzerHandler handlerRequests, FrequencyDistributionRepository frequencyRepository) {
        this.stub = stub;
        this.configuracaoService = configuracaoService;
        this.handlerRequests = handlerRequests;
        this.frequencyRepository = frequencyRepository;
    }

    public void executeFrequencyDistribution(String idDocument, File file){
        byte[] bytes = new byte[0];
        try {
            bytes = Files.readAllBytes(Paths.get(file.getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Configuracao configuracao = configuracaoService.recuperarConfiguracao();
            Integer quantidadeNuvemDePalavras = configuracao.getQuantidadeNuvemDePalavras();
            Document document = Document.newBuilder()
                    .setIdDocument(idDocument)
                    .setFile(ByteString.copyFrom(bytes))
                    .build();
            FrequencyDistributionRequest request = FrequencyDistributionRequest.newBuilder()
                    .setDocument(document)
                    .setTotal(quantidadeNuvemDePalavras)
                    .build();

            stub.findFrequencyDistribution(request, new StreamObserver<br.edu.ifpb.lib.FrequencyDistribution>() {
                @Override
                public void onNext(br.edu.ifpb.lib.FrequencyDistribution frequencyDistribution) {
                    FrequencyDistribution fd = new FrequencyDistribution();
                    fd.setDocument(frequencyDistribution.getIdDocument());
                    fd.setMostCommons(
                        frequencyDistribution.getFrequenciesList()
                                .stream().map(AnalyzerClient.this::frequencyProtoToDomain).collect(Collectors.toList())
                    );
                    frequencyRepository.save(fd);
                }
                @Override
                public void onError(Throwable throwable) {
                    log.warn(throwable.getMessage());
                }
                @Override
                public void onCompleted() {
                    log.info("finished request to findFrequencyDistribution() in " +
                            "AnalyzerServer to Document with id->" + idDocument);
                }
            });
        } catch (EntidadeNaoEncontradaException e) {
            e.printStackTrace();
        }

    }

    private Frequency frequencyProtoToDomain(br.edu.ifpb.lib.Frequency proto){
        Frequency frequency = new Frequency();
        frequency.setWord(proto.getWord());
        frequency.setQuantity(proto.getQuantity());
        return frequency;
    }
}
