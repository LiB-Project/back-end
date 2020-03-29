package br.edu.ifpb.lib.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter
@Document(indexName = "grande_area")
public class GrandeArea {
    @Id
    private Long codigo;
    private String nome;

    public GrandeArea(Long codigo, String nome) {
        this.codigo = codigo;
        this.nome = nome;
    }

    public GrandeArea() {
    }

    @Override
    public String toString() {
        return "GrandeArea{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                '}';
    }
}
