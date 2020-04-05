package br.edu.ifpb.lib.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;


@Getter
@Setter
@Document(indexName = "documento_acessos")
public class DocumentoAcessos {
    @Id
    private String id;
    private long acessos;
}
