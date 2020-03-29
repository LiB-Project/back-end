package br.edu.ifpb.lib.domain;

public enum NivelCurso {
    GRADUACAO("Graduação"),
    ESPECIALIZACAO("Especialização"),
    MESTRADO("Mestrado"),
    DOUTORADO("Doutorado");

    private String nome;

    NivelCurso(String nome){
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
