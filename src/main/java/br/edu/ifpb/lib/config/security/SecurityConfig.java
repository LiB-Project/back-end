package br.edu.ifpb.lib.config.security;

import br.edu.ifpb.lib.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.Collections;


@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtConstants jwtConstants;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, JwtConstants jwtConstants) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtConstants = jwtConstants;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/orientador").permitAll()
                .antMatchers(HttpMethod.GET, "/api/documento").permitAll()
                .antMatchers(HttpMethod.GET, "/api/curso").permitAll()
                .antMatchers(HttpMethod.GET, "/api/estatistica").permitAll()
                .antMatchers(HttpMethod.GET, "/api/configuracao").permitAll()
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), jwtConstants))
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), customUserDetailsService, jwtConstants));
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type","errorMessage","successMessage", "X-Total-Count"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type","errorMessage","successMessage"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
