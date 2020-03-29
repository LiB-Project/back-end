package br.edu.ifpb.lib.config.security;

import br.edu.ifpb.lib.service.CustomUserDetailsService;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtConstants jwtConstants;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager, CustomUserDetailsService customUserDetailsService, JwtConstants jwtConstants) {
        super(authenticationManager);
        this.customUserDetailsService = customUserDetailsService;
        this.jwtConstants = jwtConstants;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(jwtConstants.getHeader());
        if(header == null || !header.startsWith(jwtConstants.getPrefix())){
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken(request);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthenticationToken(HttpServletRequest request) {
        String header = request.getHeader(jwtConstants.getHeader());
        if (header == null) return null;
        String token = header.replace(jwtConstants.getPrefix(), "");
        byte[] bytes = DatatypeConverter.parseBase64Binary(jwtConstants.getSecret());
        try{
            String login = Jwts.parser()
                    .setSigningKey(bytes)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(login);
            return login != null ?
                    new UsernamePasswordAuthenticationToken(login, null, userDetails.getAuthorities()) : null;
        }catch(Exception e){
            return null;
        }
    }
}
