package br.edu.ifpb.lib.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class SuperUsuarioDefaultProperties {
    @Value("${credenciais.login}")
    private String superUsuarioLogin;
    @Value("${credenciais.senha}")
    private String superUsuarioSenha;
    @Value("${credenciais.nome}")
    private String superUsuarioNome;
}
