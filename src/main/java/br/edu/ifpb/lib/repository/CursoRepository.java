package br.edu.ifpb.lib.repository;

import br.edu.ifpb.lib.domain.Curso;
import br.edu.ifpb.lib.domain.NivelCurso;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursoRepository extends ElasticsearchRepository<Curso,String> {
    List<Curso> findAllByNome(String nome);
    List<Curso> findAllByNivel(NivelCurso nivel);
}
