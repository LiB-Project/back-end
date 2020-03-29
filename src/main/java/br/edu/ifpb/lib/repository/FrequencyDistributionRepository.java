package br.edu.ifpb.lib.repository;

import br.edu.ifpb.lib.domain.FrequencyDistribution;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FrequencyDistributionRepository extends ElasticsearchRepository<FrequencyDistribution, String> {
    Optional<FrequencyDistribution> findByDocument(String document);
}
