package br.edu.ifpb.lib.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import br.edu.ifpb.lib.domain.Usuario;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private JwtConstants jwtConstants;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JwtConstants jwtConstants) {
        this.authenticationManager = authenticationManager;
        this.jwtConstants = jwtConstants;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            Usuario user = new ObjectMapper().readValue(request.getInputStream(), Usuario.class);
            return this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getLogin(), user.getSenha()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) authResult.getPrincipal();
        String login = principal.getUsername();
        String token = generateToken(login);
        response.addHeader(jwtConstants.getHeader(), jwtConstants.getPrefix() + token);
    }

    private String generateToken(String username) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime agoraComHoras = agora.plusHours(jwtConstants.getExpiration());
        Date horaGeracao = convertLocalDateTimeToDate(agora);
        Date horaExpiracao = convertLocalDateTimeToDate(agoraComHoras);

        byte[] chaveBytes = DatatypeConverter.parseBase64Binary(jwtConstants.getSecret());
        Key signingKey = new SecretKeySpec(chaveBytes, signatureAlgorithm.getJcaName());
        JwtBuilder builder = Jwts.builder().setIssuedAt(horaGeracao)
                .setSubject(username)
                .signWith(signatureAlgorithm, signingKey)
                .setExpiration(horaExpiracao);
        return builder.compact();
    }

    private Date convertLocalDateTimeToDate(LocalDateTime dt){
        Instant instant = dt.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }
}
