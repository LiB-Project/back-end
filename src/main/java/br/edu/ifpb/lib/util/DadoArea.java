package br.edu.ifpb.lib.util;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class DadoArea {

    private Long id;
    private String nome;
    private Map<String, String> subAreas;

    @Override
    public String toString() {
        return "DadoArea{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", subAreas=" + subAreas +
                '}';
    }

    public DadoArea() {

    }

    public DadoArea(Long id, String nome, Map<String, String> subAreas) {
        this.id = id;
        this.nome = nome;
        this.subAreas = subAreas;
    }

}
