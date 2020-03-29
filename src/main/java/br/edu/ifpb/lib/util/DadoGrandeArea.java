package br.edu.ifpb.lib.util;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DadoGrandeArea {

    private Long id;
    private String nome;
    private List<DadoArea> areasBasicas;

    @Override
    public String toString() {
        return "DadoGrandeArea{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", areasBasicas=" + areasBasicas +
                '}';
    }

    public DadoGrandeArea() {

    }

    public DadoGrandeArea(Long id, String nome, List<DadoArea> areasBasicas) {

        this.id = id;
        this.nome = nome;
        this.areasBasicas = areasBasicas;
    }

}
