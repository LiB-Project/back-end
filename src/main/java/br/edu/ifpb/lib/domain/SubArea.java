package br.edu.ifpb.lib.domain;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter
@Document(indexName = "subarea")
public class SubArea {
    @Id
    private Long codigo;
    private String nome;
    private Long areaBasica;

    public SubArea(Long codigo, String nome, Long idArea) {
        this.codigo = codigo;
        this.nome = nome;
        this.areaBasica = idArea;
    }

    public SubArea() {
    }

    @Override
    public String toString() {
        return "SubArea{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                ", areaBasica=" + areaBasica +
                '}';
    }
}
