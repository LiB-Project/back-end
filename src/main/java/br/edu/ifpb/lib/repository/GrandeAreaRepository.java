package br.edu.ifpb.lib.repository;

import br.edu.ifpb.lib.domain.GrandeArea;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrandeAreaRepository extends ElasticsearchRepository<GrandeArea,Long> {
}
