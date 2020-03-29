package br.edu.ifpb.lib.repository;

import br.edu.ifpb.lib.domain.Configuracao;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracaoRepository extends ElasticsearchRepository<Configuracao,String> {
}
