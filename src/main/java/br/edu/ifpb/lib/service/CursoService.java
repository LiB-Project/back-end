package br.edu.ifpb.lib.service;

import br.edu.ifpb.lib.domain.Curso;
import br.edu.ifpb.lib.domain.GrandeArea;
import br.edu.ifpb.lib.domain.NivelCurso;
import br.edu.ifpb.lib.repository.CursoRepository;
import br.edu.ifpb.lib.web.valueobject.CursoVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CursoService {

    private final CursoRepository repository;
    private final GrandeAreaService grandeAreaService;

    public CursoService(CursoRepository repository, GrandeAreaService grandeAreaService) {
        this.repository = repository;
        this.grandeAreaService = grandeAreaService;
    }

    public CursoVO cadastrar(CursoVO value){
        Curso curso = valueToEntity(value);
        Curso save = repository.save(curso);
        return entityToValue(save);
    }

    public CursoVO atualizar(CursoVO atualizado){
        Curso curso = valueToEntity(atualizado);
        Curso save = repository.save(curso);
        return entityToValue(save);
    }

    public CursoVO buscarPorId(String id){
        Optional<Curso> byId = repository.findById(id);
        if(byId.isPresent())
            return entityToValue(byId.get());
        else
            return null;
    }

    public void deletar(String id){
        repository.deleteById(id);
    }

    public List<CursoVO> listar(){
        List<Curso> list = new ArrayList<>();
        repository.findAll().forEach(list::add);
        List<CursoVO> collect = list.stream().map(entity -> entityToValue(entity)).collect(Collectors.toList());
        return collect;
    }

    public Page<CursoVO> listaPaginada(Pageable pageable){
        return repository.findAll(pageable).map(this::entityToValue);
    }

    private Curso valueToEntity(CursoVO cursoVO){
        Curso curso = new Curso();
        curso.setId(cursoVO.getId());
        curso.setNome(cursoVO.getNome());
        curso.setSigla(cursoVO.getSigla());
        curso.setDescricao(cursoVO.getDescricao());
        curso.setCodigoGrandeArea(cursoVO.getGrandeArea().getCodigo());
        curso.setNivel(cursoVO.getNivel());
        return curso;
    }
    private CursoVO entityToValue(Curso entity){
        CursoVO cursoVO = new CursoVO();
        cursoVO.setId(entity.getId());
        cursoVO.setNome(entity.getNome());
        cursoVO.setDescricao(entity.getDescricao());
        cursoVO.setSigla(entity.getSigla());
        Optional<GrandeArea> grandeArea = grandeAreaService.buscarPorCodigo(entity.getCodigoGrandeArea());
        if(grandeArea.isPresent())
            cursoVO.setGrandeArea(grandeArea.get());
        cursoVO.setNivel(entity.getNivel());
        return cursoVO;
    }

    public boolean cursoExiste(String id) {
        return repository.existsById(id);
    }

    public List<CursoVO> listarPorNivel(NivelCurso nivel) {
        return repository.findAllByNivel(nivel)
                .parallelStream()
                .map(this::entityToValue)
                .collect(Collectors.toList());
    }
}
