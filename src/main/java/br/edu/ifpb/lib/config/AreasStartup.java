package br.edu.ifpb.lib.config;

import br.edu.ifpb.lib.repository.SubAreaRepository;
import br.edu.ifpb.lib.util.ReadJSON;
import lombok.extern.slf4j.Slf4j;
import br.edu.ifpb.lib.repository.AreaBasicaRepository;
import br.edu.ifpb.lib.repository.GrandeAreaRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AreasStartup implements ApplicationListener<ContextRefreshedEvent> {
    private final ReadJSON json;
    private final GrandeAreaRepository grandeAreaRepository;
    private final AreaBasicaRepository areaBasicaRepository;
    private final SubAreaRepository subAreaRepository;

    public AreasStartup(ReadJSON readJSON, GrandeAreaRepository grandeAreaRepository, AreaBasicaRepository areaBasicaRepository, SubAreaRepository subAreaRepository) {
        this.json = readJSON;
        this.grandeAreaRepository = grandeAreaRepository;
        this.areaBasicaRepository = areaBasicaRepository;
        this.subAreaRepository = subAreaRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("INSERINDO ÁREAS NO BANCO DE DADOS");
        grandeAreaRepository.saveAll(json.objetosGrandeArea());
        areaBasicaRepository.saveAll(json.objetosAreaDeConhecimento());
        subAreaRepository.saveAll(json.objetosSubArea());
    }
}
