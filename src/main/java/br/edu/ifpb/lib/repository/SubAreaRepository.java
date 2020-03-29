package br.edu.ifpb.lib.repository;

import br.edu.ifpb.lib.domain.SubArea;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SubAreaRepository extends ElasticsearchRepository<SubArea,Long> {
    List<SubArea> findAllByAreaBasica(@Param("areaBasica") Long areaBasica);
}
