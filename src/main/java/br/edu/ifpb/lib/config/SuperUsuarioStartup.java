package br.edu.ifpb.lib.config;

import br.edu.ifpb.lib.domain.Role;
import br.edu.ifpb.lib.domain.Usuario;
import br.edu.ifpb.lib.service.UserService;
import br.edu.ifpb.lib.service.exceptions.EntidadeExistenteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Slf4j
public class SuperUsuarioStartup implements ApplicationListener<ContextRefreshedEvent>{

    private final SuperUsuarioDefaultProperties superUsuarioDefaultProperties;
    private final UserService userService;

    public SuperUsuarioStartup(SuperUsuarioDefaultProperties superUsuarioDefaultProperties, UserService userService) {
        this.superUsuarioDefaultProperties = superUsuarioDefaultProperties;
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        Usuario usuario = new Usuario();
        usuario.setLogin(superUsuarioDefaultProperties.getSuperUsuarioLogin());
        usuario.setSenha(superUsuarioDefaultProperties.getSuperUsuarioSenha());
        usuario.setRoles(Arrays.asList(Role.ROLE_SUPER_USER));
        usuario.setNome(superUsuarioDefaultProperties.getSuperUsuarioNome());

        try {
            log.info("DEFININDO SUPER USUÁRIO DEFAULT NO SISTEMA");
            userService.cadastrar(usuario);
        } catch (EntidadeExistenteException e) {
            log.info("JÁ EXISTE O SUPER USUÁRIO DEFAULT NO SISTEMA");
        }
    }
}
