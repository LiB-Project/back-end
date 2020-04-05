package br.edu.ifpb.lib.repository;

import br.edu.ifpb.lib.domain.Documento;
import br.edu.ifpb.lib.domain.DocumentoAcessos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DocumentoAcessosRepository extends ElasticsearchRepository<DocumentoAcessos, String> {

}
