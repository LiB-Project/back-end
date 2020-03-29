package br.edu.ifpb.lib.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class ConfiguracaoProperties {
    @Value("${configuracao.tituloSistema}")
    private String tituloSistema;
    @Value("${configuracao.quantidadeNuvemDePalavras}")
    private Integer quantidadeNuvemDePalavras;
    @Value("${configuracao.favicon}")
    private String faviconResource;
    @Value("${configuracao.icon}")
    private String resourceIcon;
    @Value("${configuracao.logomarca}")
    private String resourceLogomarca;
    @Value("${configuracao.html-sobre}")
    private String htmlSobre;
    @Value("${configuracao.nome-instituicao}")
    private String nomeInstitucao;
    @Value("${configuracao.sigla-instituicao}")
    private String siglaInstituicao;
}
