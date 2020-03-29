package br.edu.ifpb.lib.service;

import br.edu.ifpb.lib.domain.AreaBasica;
import br.edu.ifpb.lib.domain.GrandeArea;
import br.edu.ifpb.lib.domain.SubArea;
import br.edu.ifpb.lib.repository.AreaBasicaRepository;
import br.edu.ifpb.lib.repository.GrandeAreaRepository;
import br.edu.ifpb.lib.repository.SubAreaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GrandeAreaService {

    private final GrandeAreaRepository grandeAreaRepository;
    private final AreaBasicaRepository areaBasicaRepository;
    private final SubAreaRepository subAreaRepository;
    private final SubAreaService subAreaService;

    public GrandeAreaService(GrandeAreaRepository grandeAreaRepository, AreaBasicaRepository areaBasicaRepository, SubAreaRepository subAreaRepository, SubAreaService subAreaService) {
        this.grandeAreaRepository = grandeAreaRepository;
        this.areaBasicaRepository = areaBasicaRepository;
        this.subAreaRepository = subAreaRepository;
        this.subAreaService = subAreaService;
    }

    public GrandeArea salvar(GrandeArea grandeArea){
        return grandeAreaRepository.save(grandeArea);
    }

    public Optional<GrandeArea> buscarPorCodigo(Long codigo){
        return grandeAreaRepository.findById(codigo);
    }

    public void deletar(Long codigo){
        grandeAreaRepository.deleteById(codigo);
    }

    public List<GrandeArea> listarTodas(){
        List<GrandeArea> list = new ArrayList<>();
        grandeAreaRepository.findAll().forEach(list::add);
        return list;
    }

    public List<SubArea> listarSubAreasDeGrandeArea(Long codigoGrandeArea) {
        List<SubArea> subAreaList = new ArrayList<>();

        List<AreaBasica> areaBasicaList = areaBasicaRepository.findAllByGrandeArea(codigoGrandeArea);
        List<SubArea> subAreas = subAreaService.listarTodas();

        areaBasicaList.parallelStream().forEach(area -> {
            List<SubArea> collect = subAreas.parallelStream().filter(sub -> sub.getAreaBasica().equals(area.getCodigo())).collect(Collectors.toList());
            subAreaList.addAll(collect);
        });

        return subAreaList.parallelStream().distinct().collect(Collectors.toList());
    }

}
