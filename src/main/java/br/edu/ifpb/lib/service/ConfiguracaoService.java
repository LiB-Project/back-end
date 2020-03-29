package br.edu.ifpb.lib.service;

import br.edu.ifpb.lib.domain.Configuracao;
import br.edu.ifpb.lib.repository.ConfiguracaoRepository;
import br.edu.ifpb.lib.service.exceptions.EntidadeNaoEncontradaException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConfiguracaoService {

    private final ConfiguracaoRepository configuracaoRepository;

    public ConfiguracaoService(ConfiguracaoRepository configuracaoRepository) {
        this.configuracaoRepository = configuracaoRepository;
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

}
