package br.edu.ifpb.lib.domain;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;

@Data
@Document(indexName = "autor")
public class Autor implements Serializable {
    private String nome;
    private String matricula;
}
