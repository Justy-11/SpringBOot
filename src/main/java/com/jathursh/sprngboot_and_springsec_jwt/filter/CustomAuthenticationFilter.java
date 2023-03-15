package com.jathursh.sprngboot_and_springsec_jwt.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

// -- added on 9/3/2023 --
@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        log.info("Username : {}",username);
        log.info("Password : {}",password);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username,password);
        return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        User user = (User)authResult.getPrincipal();  // user from successful authentication, user from the UserDetails

        // ** In reality the secret is kept in a hidden file - not seen to everyone **
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

        String access_token  = JWT.create().withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))  // 10 * 60 * 1000 - 10 minutes expiration time
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);

        String refresh_token  = JWT.create().withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))  // 30 * 60 * 1000 - 30 minutes expiration time
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);

        /*
        // to set the access_token and access_token in the headers in postman
        response.addHeader("access_token",access_token);
        response.addHeader("refresh_token",refresh_token);*/

        // above 2 lines using json body - using object mapper
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
}
