package br.edu.ifpb.lib.service.grpc;

import lombok.extern.slf4j.Slf4j;
import br.edu.ifpb.lib.repository.FrequencyDistributionRepository;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AnalyzerHandler {

    private final FrequencyDistributionRepository repository;

    public AnalyzerHandler(FrequencyDistributionRepository repository) {
        this.repository = repository;
    }

//    public StreamObserver<FrequencyDistribution> handleFrequencyDistribution(){
//        return new StreamObserver<FrequencyDistribution>() {
//            @Override
//            public void onNext(FrequencyDistribution frequencyDistribution) {
//                log.info(frequencyDistribution.toString());
//                net.lib.domain.FrequencyDistribution fd = new net.lib.domain.FrequencyDistribution();
//                fd.setDocument(frequencyDistribution.getIdDocument());
//                fd.setMostCommons(frequencyDistribution.getFrequenciesList());
//                repository.save(fd);
//            }
//            @Override
//            public void onError(Throwable throwable) {
//                log.warn(throwable.getMessage());
//            }
//            @Override
//            public void onCompleted() {
////                log.info("finished request to findFrequencyDistribution() in " +
////                        "AnalyzerServer to Document with id->" + idDocument);
//            }
//        };
//    }
}
