package br.edu.ifpb.lib.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.edu.ifpb.lib.domain.AreaBasica;
import br.edu.ifpb.lib.domain.GrandeArea;
import br.edu.ifpb.lib.domain.SubArea;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ReadJSON {
    private ObjectMapper mapper;
    private List<DadoGrandeArea> dadoGrandesAreaList;

    @PostConstruct
    public void initialize() {
        try {
            this.mapper = new ObjectMapper();
            InputStream resource =
                    getClass().getClassLoader().getResourceAsStream("areas.json");
            this.dadoGrandesAreaList =
                    mapper.readValue(resource, new TypeReference<ArrayList<DadoGrandeArea>>() {});
            resource.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<GrandeArea> objetosGrandeArea() {
        List<GrandeArea> retorno = new ArrayList<GrandeArea>();
        for (DadoGrandeArea d : dadoGrandesAreaList) {
            GrandeArea grandeArea = new GrandeArea(d.getId(), d.getNome());
            retorno.add(grandeArea);
        }
        return retorno;
    }

    public List<AreaBasica> objetosAreaDeConhecimento() {
        List<AreaBasica> areas = new ArrayList<>();
        for (DadoGrandeArea d : dadoGrandesAreaList) {
            Long idGrandeArea = d.getId();
            List<DadoArea> areasBasicas = d.getAreasBasicas();
            for (DadoArea dadoArea : areasBasicas) {
                areas.add(new AreaBasica(dadoArea.getId(), dadoArea.getNome(), idGrandeArea));
            }
        }
        return areas;
    }

    public List<SubArea> objetosSubArea() {
        List<SubArea> subAreas = new ArrayList<>();
        for (DadoGrandeArea d : dadoGrandesAreaList) {
            List<DadoArea> areasBasicas = d.getAreasBasicas();
            for (DadoArea dadoArea : areasBasicas) {
                Long idArea = dadoArea.getId();
                for (Map.Entry<String, String> subArea : dadoArea.getSubAreas().entrySet()) {
                    subAreas.add(new SubArea(Long.valueOf(subArea.getKey()), subArea.getValue(), idArea));
                }
            }
        }
        return subAreas;
    }

}
