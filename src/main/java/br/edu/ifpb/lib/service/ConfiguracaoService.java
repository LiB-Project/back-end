package br.edu.ifpb.lib.service;

import br.edu.ifpb.lib.config.ConfiguracaoProperties;
import br.edu.ifpb.lib.domain.Configuracao;
import br.edu.ifpb.lib.repository.ConfiguracaoRepository;
import br.edu.ifpb.lib.service.exceptions.EntidadeNaoEncontradaException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class ConfiguracaoService {

    private final ConfiguracaoRepository configuracaoRepository;
    private final ConfiguracaoProperties configuracaoProperties;

    public ConfiguracaoService(ConfiguracaoRepository configuracaoRepository, ConfiguracaoProperties configuracaoProperties) {
        this.configuracaoRepository = configuracaoRepository;
        this.configuracaoProperties = configuracaoProperties;
    }

    public Configuracao salvar(Configuracao config){
        Configuracao save = configuracaoRepository.save(config);
        return save;
    }

    public Configuracao atualizar(Configuracao atualizado){
        Configuracao updated = configuracaoRepository.save(atualizado);
        return updated;
    }

    public Configuracao recuperarConfiguracao() throws EntidadeNaoEncontradaException {
        List<Configuracao> list = new ArrayList<>();
        configuracaoRepository.findAll().forEach(list::add);
        Optional<Configuracao> first = list.parallelStream().findFirst();
        if(first.isPresent())
            return first.get();
        else
            throw new EntidadeNaoEncontradaException("A configuração da biblioteca ainda não foi feita!");
    }

    public void restaurar() {
        Configuracao configuracao = null;
        try {
            configuracao = recuperarConfiguracao();
            configuracaoRepository.delete(configuracao);
        } catch (EntidadeNaoEncontradaException e) {
            log.warn("Não existe configuração");
        }
        inicializarConfiguracao();
    }

    public void inicializarConfiguracao(){
        log.info("[ Definindo configuracao padrao! ]");
        Configuracao configuracao = new Configuracao();
        log.info("[ Lendo o arquivo HTML da pagina sobre ]");
        InputStream resource =
                getClass().getClassLoader()
                        .getResourceAsStream(configuracaoProperties.getHtmlSobre());
        if(resource != null) {
            StringBuilder textBuilder = new StringBuilder();
            try (Reader reader = new BufferedReader(new InputStreamReader
                    (resource, Charset.forName(StandardCharsets.UTF_8.name())))) {
                int c = 0;
                while ((c = reader.read()) != -1) {
                    textBuilder.append((char) c);
                }
            } catch (IOException ex) {
                log.error("[ Ocorreu um problema ao ler o HTML da pagina 'sobre.html' ]");
            }
            try {
                resource.close();
                configuracao.setHtmlPaginaSobre(textBuilder.toString());
            } catch (IOException ex) {
                log.error("[ Ocorreu um problema ao fechar o arquivo HTML da pagina 'sobre.html' ]");
            }
        }

        log.info("[ Lendo o arquivo favicon ]");
        File faviconFile = new File(getClass().getClassLoader()
                .getResource(configuracaoProperties.getFaviconResource())
                .getFile());
        try {
            byte[] bytesFavicon = FileUtils.readFileToByteArray(faviconFile);
            String base64Favicon = Base64.encodeBase64String(bytesFavicon);
            configuracao.setFaviconBase64(base64Favicon);
        } catch (IOException ex) {
            log.error("[ Nao foi possivel ler o arquivo favicon ]");
        }

        log.info("[ Lendo o arquivo de icone ]");
        File iconFile = new File(getClass().getClassLoader()
                .getResource(configuracaoProperties.getResourceIcon())
                .getFile());
        try {
            byte[] bytesIcon = FileUtils.readFileToByteArray(iconFile);
            String base64Icon = Base64.encodeBase64String(bytesIcon);
            configuracao.setIconeBase64(base64Icon);
        } catch (IOException ex) {
            log.error("[ Nao foi possivel ler o arquivo de icone ]");
        }

        log.info("[ Lendo o arquivo de logomarca ]");
        File logomarcaFile = new File(getClass().getClassLoader()
                .getResource(configuracaoProperties.getResourceLogomarca())
                .getFile());
        try {
            byte[] bytesLogomarca = FileUtils.readFileToByteArray(logomarcaFile);
            String base64Logomarca = Base64.encodeBase64String(bytesLogomarca);
            configuracao.setLogomarcaBase64(base64Logomarca);
        } catch (IOException ex) {
            log.error("[ Nao foi possivel ler o arquivo de logomarca ]");
        }

        configuracao.setTituloSistema(configuracaoProperties.getTituloSistema());
        configuracao.setQuantidadeNuvemDePalavras(configuracaoProperties.getQuantidadeNuvemDePalavras());
        configuracao.setNomeInstituicao(configuracaoProperties.getNomeInstitucao());
        configuracao.setSiglaInstituicao(configuracaoProperties.getSiglaInstituicao());

        salvar(configuracao);
    }
}
