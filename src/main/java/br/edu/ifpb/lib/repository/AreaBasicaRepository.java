package br.edu.ifpb.lib.repository;

import br.edu.ifpb.lib.domain.AreaBasica;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AreaBasicaRepository extends ElasticsearchRepository<AreaBasica,Long> {

    List<AreaBasica> findAllByGrandeArea(@Param("grandeArea") Long grandeArea);

}
