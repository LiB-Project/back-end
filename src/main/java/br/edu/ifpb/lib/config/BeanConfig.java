package br.edu.ifpb.lib.config;

import br.edu.ifpb.lib.config.security.JwtConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public JwtConstants provideJwtConfig(){
        return new JwtConstants();
    }

    @Bean
    public ConfiguracaoProperties provideConfiguracaoProperties() { return new ConfiguracaoProperties(); }

}
