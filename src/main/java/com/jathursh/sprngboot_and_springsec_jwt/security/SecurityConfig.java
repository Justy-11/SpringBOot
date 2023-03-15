package com.jathursh.sprngboot_and_springsec_jwt.security;

import com.jathursh.sprngboot_and_springsec_jwt.filter.CustomAuthenticationFilter;
import com.jathursh.sprngboot_and_springsec_jwt.filter.CustomAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collections;

// -- added on 9/3/2023--
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String[] WHITE_LIST_URLS = {
            "/api/login/**",
            "/api/token/refresh/**",
            "/api/hello/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/webjars/**",
            "/swagger-resources/**"
    };

    // added 15/3/2023
    private static final String[] ADMIN_ALLOWED_URLS = {
            "/api/users/**",
            "/api/role/save",
            "/api/user/save"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // "/login" - api is coming from the UsernamePasswordAuthenticationFilter.class built in (we extended CustomAuthenticationFilter.java from UsernamePasswordAuthenticationFilter.class)
        // we did not have the "/login" in our controller
        // so, to override the api below code (2 lines) is used
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager());
        customAuthenticationFilter.setFilterProcessesUrl("/api/login");


        return http.csrf().disable()
                //.cors().disable()   // added on 13/3/2023
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests().requestMatchers(WHITE_LIST_URLS).permitAll()
                .and()
//                .authorizeHttpRequests().requestMatchers("/api/user/**").hasAnyAuthority("ROLE_USER")
//                .and()
                .authorizeHttpRequests().requestMatchers(ADMIN_ALLOWED_URLS).hasAnyAuthority("ROLE_ADMIN")
                .and()
                .authorizeHttpRequests().anyRequest().authenticated()
                .and()
                .addFilter(customAuthenticationFilter)
                .addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)  // make sure it comes before the authentication filter, as CustomAuthorizationFilter intercepts before it
                .build();
    }

    // authenticationManager() bean that uses a ProviderManager to authenticate users, and an authenticationProvider() bean that provides the UserDetailsService and PasswordEncoder to the DaoAuthenticationProvider.
    //With this configuration, you should be able to customize the Spring Security configuration without extending any classes.

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return new ProviderManager(Collections.singletonList(authenticationProvider()));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);  // help in the authentication of users, by load their details by username
        provider.setPasswordEncoder(passwordEncoder); // checks the password of the user details, with the Encoded password using passwordEncoder
        return provider;
    }

    // authentication - whenever user login to the system jwt token is given verifying the credential
    // authorization - subsequent requests using that token, verify that token and give access to the resources as per the permissions if the token is valid

}
