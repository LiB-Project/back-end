package br.edu.ifpb.lib.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter
@Document(indexName = "curso")
public class Curso {
    @Id
    private String id;
    private String sigla;
    private String nome;
    private String descricao;
    private NivelCurso nivel;
    private Long codigoGrandeArea;
}
