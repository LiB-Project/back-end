package br.edu.ifpb.lib.config;

import br.edu.ifpb.lib.service.ConfiguracaoService;
import br.edu.ifpb.lib.service.exceptions.EntidadeNaoEncontradaException;
import lombok.extern.slf4j.Slf4j;
import br.edu.ifpb.lib.domain.Configuracao;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class ConfiguracaoStartup implements ApplicationListener<ContextRefreshedEvent> {

    private final ConfiguracaoService configuracaoService;

    public ConfiguracaoStartup(ConfiguracaoService configuracaoService) {
        this.configuracaoService = configuracaoService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            configuracaoService.recuperarConfiguracao();
        } catch (EntidadeNaoEncontradaException e) {
            log.info("[ Nenhuma configuracao de personalizacao do sistema foi encontrada! ]");
            configuracaoService.inicializarConfiguracao();
        }
    }
}
