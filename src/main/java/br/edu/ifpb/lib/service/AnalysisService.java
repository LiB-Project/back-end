package br.edu.ifpb.lib.service;

import br.edu.ifpb.lib.domain.FrequencyDistribution;
import br.edu.ifpb.lib.repository.FrequencyDistributionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AnalysisService {
    private final FrequencyDistributionRepository frequencyRepository;

    public AnalysisService(FrequencyDistributionRepository frequencyRepository) {
        this.frequencyRepository = frequencyRepository;
    }

    public FrequencyDistribution getFrequencyByDocument(String idDocument){
        Optional<FrequencyDistribution> byDocument = frequencyRepository.findByDocument(idDocument);
        if(byDocument.isPresent())
            return byDocument.get();
        else
            return null;

    }
}
