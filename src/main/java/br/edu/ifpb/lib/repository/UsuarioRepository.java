package br.edu.ifpb.lib.repository;

import br.edu.ifpb.lib.domain.Usuario;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends ElasticsearchRepository<Usuario,String> {
    Optional<Usuario> findByLogin(String login);
}
