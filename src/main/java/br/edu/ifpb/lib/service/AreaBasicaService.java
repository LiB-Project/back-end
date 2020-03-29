package br.edu.ifpb.lib.service;

import br.edu.ifpb.lib.domain.AreaBasica;
import br.edu.ifpb.lib.repository.AreaBasicaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AreaBasicaService {
    private final AreaBasicaRepository areaBasicaRepository;

    public AreaBasicaService(AreaBasicaRepository areaBasicaRepository) {
        this.areaBasicaRepository = areaBasicaRepository;
    }

    public AreaBasica salvar(AreaBasica areaBasica){
        return areaBasicaRepository.save(areaBasica);
    }

    public Optional<AreaBasica> buscarPorCodigo(Long codigo){
        return areaBasicaRepository.findById(codigo);
    }

    public void deletar(Long codigo){
        areaBasicaRepository.deleteById(codigo);
    }

    public List<AreaBasica> listarTodas(){
        List<AreaBasica> list = new ArrayList<>();
        areaBasicaRepository.findAll().forEach(list::add);
        return list;
    }

    public List<AreaBasica> listarTodasGrandeArea(Long grandeArea){
        return areaBasicaRepository.findAllByGrandeArea(grandeArea);
    }

}
