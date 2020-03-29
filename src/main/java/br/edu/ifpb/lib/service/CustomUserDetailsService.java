package br.edu.ifpb.lib.service;

import br.edu.ifpb.lib.repository.UsuarioRepository;
import br.edu.ifpb.lib.domain.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<Usuario> userOptional = usuarioRepository.findByLogin(login);
        if(!userOptional.isPresent()){
            throw new UsernameNotFoundException("Usuário não encontrado");
        }else{
            Usuario usuario = userOptional.get();
            String[] roles = usuario.getRoles().stream().map(role -> role.toString()).toArray(String[]::new);
            List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(roles);
            return new org.springframework.security.core.userdetails.User(usuario.getLogin(), usuario.getSenha(), authorityList);
        }
    }

}
