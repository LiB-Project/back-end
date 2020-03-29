package br.edu.ifpb.lib.repository;

import br.edu.ifpb.lib.domain.Documento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface DocumentoRepository extends ElasticsearchRepository<Documento, String> {
    Page<Documento> findAllByCursoId(Pageable pageable, String cursoId);
    Page<Documento> findAllByCursoIdIn(Pageable pageable, List<String> cursoId);
    Page<Documento> findAllByOrientadorIdIn(Pageable pageable, List<String> orientadorId);
    int countAllByAnoPublicacaoEquals(int anoPublicacao);
    int countAllByCursoIdEqualsAndAnoPublicacaoEquals(String cursoId, int anoPublicacao);
    int countBySubAreasIdIsContaining(List<Long> subAreasId);
}
