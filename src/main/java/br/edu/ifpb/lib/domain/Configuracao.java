package br.edu.ifpb.lib.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter
@Document(indexName = "configuracao")
public class Configuracao {
    @Id
    private String id;
    private String tituloSistema;
    private Integer quantidadeNuvemDePalavras;
    private String faviconBase64;
    private String iconeBase64;
    private String logomarcaBase64;
    private String nomeInstituicao;
    private String siglaInstituicao;
    private String htmlPaginaSobre;
}
