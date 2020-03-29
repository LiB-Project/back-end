package br.edu.ifpb.lib.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class DiretoryConstantsConfig {

    @Value("${configuracao.diretorio-documentos}")
    private String documentsDirectory;

}
