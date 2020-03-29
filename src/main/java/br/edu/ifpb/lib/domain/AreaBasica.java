package br.edu.ifpb.lib.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter
@Document(indexName = "area_basica")
public class AreaBasica {

    @Id
    private Long codigo;
    private String nome;
    private Long grandeArea;

    public AreaBasica(Long codigo, String nome, Long idGrandeArea) {
        this.codigo = codigo;
        this.nome = nome;
        this.grandeArea = idGrandeArea;
    }

    public AreaBasica() {
    }

    @Override
    public String toString() {
        return "AreaBasica{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                ", grandeArea=" + grandeArea +
                '}';
    }
}
