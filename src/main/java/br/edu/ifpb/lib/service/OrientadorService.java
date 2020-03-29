package br.edu.ifpb.lib.service;

import br.edu.ifpb.lib.domain.Orientador;
import br.edu.ifpb.lib.repository.OrientadorRepository;
import br.edu.ifpb.lib.service.exceptions.EntidadeExistenteException;
import br.edu.ifpb.lib.service.exceptions.EntidadeNaoEncontradaException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrientadorService {

    private final OrientadorRepository repository;

    public OrientadorService(OrientadorRepository repository) {
        this.repository = repository;
    }

    public Orientador cadastrar(Orientador orientador) throws EntidadeExistenteException {
        boolean valido = validarMatricula(orientador.getMatricula());
        if(!valido){
           throw new EntidadeExistenteException("Já existe um orientador com a matrícula informada");
        }else{
            return repository.save(orientador);
        }
    }

    public Orientador atualizar(Orientador atualizado) throws EntidadeExistenteException {
        try {
            Orientador orientador = buscarPorMatricula(atualizado.getMatricula());
            if(!orientador.getId().equals(atualizado.getId())){
                throw new EntidadeExistenteException("Já existe um orientador com a matrícula informada");
            }else{
                return repository.save(atualizado);
            }
        } catch (EntidadeNaoEncontradaException e) {
            return repository.save(atualizado);
        }
    }


    public void deletar(String matricula){
        repository.deleteById(matricula);
    }

    public Orientador buscarPorMatricula(String matricula) throws EntidadeNaoEncontradaException {
        Optional<Orientador> byMatricula = repository.findByMatricula(matricula);
        if(!byMatricula.isPresent()){
            throw new EntidadeNaoEncontradaException("Não existe um orientador com a matrícula informada");
        }else{
            return byMatricula.get();
        }
    }

    private boolean validarMatricula(String matricula){
        Optional<Orientador> byMatricula = repository.findByMatricula(matricula);
        if(byMatricula.isPresent())
            return false;
        else
            return true;
    }

    public Orientador buscarPorId(String id) throws EntidadeNaoEncontradaException {
        Optional<Orientador> byId = repository.findById(id);
        if(!byId.isPresent()){
            throw new EntidadeNaoEncontradaException("Orientador não encontrado");
        }else{
            return byId.get();
        }
    }

    public List<Orientador> listar(){
        List<Orientador> list = new ArrayList<>();
        repository.findAll().forEach(list::add);
        return list;
    }

    public Page<Orientador> listaPaginada(Pageable pageable){
        return repository.findAll(pageable);
    }
}
