package br.edu.ifpb.lib.service;

import br.edu.ifpb.lib.repository.UsuarioRepository;
import br.edu.ifpb.lib.service.exceptions.EntidadeExistenteException;
import br.edu.ifpb.lib.service.exceptions.EntidadeNaoEncontradaException;
import br.edu.ifpb.lib.domain.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    public Usuario cadastrar(Usuario u) throws EntidadeExistenteException {
        boolean existsByLogin = usuarioRepository.findByLogin(u.getLogin()).isPresent();
        if(existsByLogin)
            throw new EntidadeExistenteException("Já existe um usuário cadastrado com esse login!");

        String encode = bCryptPasswordEncoder.encode(u.getSenha());
        u.setSenha(encode);
        return this.usuarioRepository.save(u);
    }

    public Usuario atualizar(Usuario usuario) throws EntidadeExistenteException {
        Optional<Usuario> byLogin = usuarioRepository.findByLogin(usuario.getLogin());
        if(byLogin.isPresent() && !usuario.getId().equals(byLogin.get().getId()))
            throw new EntidadeExistenteException("Já existe um usuário cadastrado com esse login!");
        return usuarioRepository.save(usuario);
    }

    public Page<Usuario> listagemPaginada(Pageable pageable){
        return usuarioRepository.findAll(pageable);
    }

    public void deletarUsuario(String id){
        this.usuarioRepository.deleteById(id);
    }

    public Usuario buscarPorLogin(String login) throws EntidadeNaoEncontradaException {
        Optional<Usuario> byLogin = this.usuarioRepository.findByLogin(login);
        if(!byLogin.isPresent()){
            throw new EntidadeNaoEncontradaException("O usuário informado não existe!");
        }
        return byLogin.get();
    }

    public Usuario buscarPorId(String id) throws EntidadeNaoEncontradaException {
        Optional<Usuario> byId = usuarioRepository.findById(id);
        if(!byId.isPresent()){
            throw new EntidadeNaoEncontradaException("O usuário informado não existe!");
        }
        return byId.get();
    }
}
