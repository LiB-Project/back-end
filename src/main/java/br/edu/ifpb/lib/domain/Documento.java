package br.edu.ifpb.lib.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@Document(indexName = "documento")
public class Documento {

    @Id
    private String id;
    private String titulo;
    private Autor autor;
    private String cursoId;
    private String conteudo;
    private String resumo;
    private LocalDate dataApresentacao;
    private LocalDateTime dataPublicacao;
    private int anoPublicacao;
    private String orientadorId;
    private String coorientadorId;
    private String isbn;
    private List<Long> subAreasId = new ArrayList<>();
    private List<String> palavrasChave = new ArrayList<>();
    private String pathArquivo;

    public void set_score(Double _score) {
    }

    public void setAnoPublicacao(int anoPublicacao) {
        this.anoPublicacao = this.getDataApresentacao().getYear();
    }

    @Override
    public String toString() {
        return "Documento{" +
                "id='" + id + '\'' +
                ", titulo='" + titulo + '\'' +
                ", autor=" + autor +
                ", cursoId='" + cursoId + '\'' +
                ", dataApresentacao=" + dataApresentacao +
                ", dataPublicacao=" + dataPublicacao +
                ", orientadorId='" + orientadorId + '\'' +
                ", coorientadorId='" + coorientadorId + '\'' +
                ", isbn='" + isbn + '\'' +
                ", subAreasId=" + subAreasId +
                ", palavrasChave=" + palavrasChave +
                ", pathArquivo='" + pathArquivo + '\'' +
                '}';
    }
}
