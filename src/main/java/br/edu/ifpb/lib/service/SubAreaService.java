package br.edu.ifpb.lib.service;

import br.edu.ifpb.lib.repository.SubAreaRepository;
import br.edu.ifpb.lib.domain.SubArea;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SubAreaService {
    private final SubAreaRepository subAreaRepository;

    public SubAreaService(SubAreaRepository subAreaRepository) {
        this.subAreaRepository = subAreaRepository;
    }

    public SubArea salvar(SubArea subArea){
        return subAreaRepository.save(subArea);
    }

    public Optional<SubArea> buscarPorCodigo(Long codigo){
        return subAreaRepository.findById(codigo);
    }

    public void deletar(Long codigo){
        subAreaRepository.deleteById(codigo);
    }

    public List<SubArea> listarTodas(){
        List<SubArea> list = new ArrayList<>();
        subAreaRepository.findAll().forEach(list::add);
        return list;
    }

    public List<SubArea> listarTodasAreaBasica(Long areaBasica){
        return subAreaRepository.findAllByAreaBasica(areaBasica);
    }

}
