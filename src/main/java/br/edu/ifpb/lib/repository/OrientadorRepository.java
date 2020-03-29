package br.edu.ifpb.lib.repository;

import br.edu.ifpb.lib.domain.Orientador;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface OrientadorRepository extends ElasticsearchRepository<Orientador,String> {

    Optional<Orientador> findByMatricula(String matricula);
    List<Orientador> findAllByNome(String nome);

}
