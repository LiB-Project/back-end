package br.edu.ifpb.lib.config.security;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class JwtConstants {
    @Value("${security.jwt.uri}")
    private String uri;
    @Value("${security.jwt.header}")
    private String header;
    @Value("${security.jwt.prefix}")
    private String prefix;
    @Value("${security.jwt.expiration}")
    private int expiration;
    @Value("${security.jwt.secret}")
    private String secret;
}
