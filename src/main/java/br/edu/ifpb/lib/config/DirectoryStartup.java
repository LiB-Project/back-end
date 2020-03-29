package br.edu.ifpb.lib.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Log4j2
public class DirectoryStartup implements ApplicationListener<ContextRefreshedEvent> {

    private final DiretoryConstantsConfig diretoryConstants;

    public DirectoryStartup(DiretoryConstantsConfig diretoryConstants) {
        this.diretoryConstants = diretoryConstants;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        String path = diretoryConstants.getDocumentsDirectory();
        File dir = new File(path);
        if(!dir.exists()){
            if(dir.mkdirs()){
                log.info("Diretório "+ path + " criado para salvar os documents");
            }else{
                log.info("Não foi possível criar o diretório: "
                        + path + " para que os arquivos sejam salvos!");
            }

        }
    }
}
