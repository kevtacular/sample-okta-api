package com.example.oktaapi.config;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import com.example.oktaapi.security.JwtUtils;

@TestConfiguration
@EnableMethodSecurity
public class TestSecurityConfig {

    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<String> authorities = new ArrayList<>();
            
            // Add scopes
            if (jwt.getClaim("scp") instanceof List) {
                List<String> scopes = jwt.getClaimAsStringList("scp");
                scopes.stream()
                    .map(scope -> "SCOPE_" + scope)
                    .forEach(authorities::add);
            }
            
            // Add groups
            if (jwt.getClaim("groups") instanceof List) {
                authorities.addAll(jwt.getClaimAsStringList("groups"));
            }
            
            return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        });
        return converter;
    }
}