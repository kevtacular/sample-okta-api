package com.example.oktaapi.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

/**
 * Security configuration class for the application.
 * 
 * <p>Key Features:
 * <ul>
 *   <li>Configures the application to use OAuth2 resource server with JWT authentication.</li>
 *   <li>Configures session management to use {@link SessionCreationPolicy#STATELESS}.</li>
 *   <li>Defines authorization rules for different API endpoints:
 *     <ul>
 *       <li>Endpoints under "/api/public/**" are accessible to everyone without authentication.</li>
 *       <li>Endpoints under "/api/private/**" require authentication. (See {@link com.example.oktaapi.controller.SecuredController}
 *           endpoints for further restrictions.)</li>
 *       <li>Endpoints under "/api/admin/**" require the user to have one of the specified admin authorities.</li>
 *       <li>All other requests require authentication.</li>
 *     </ul>
 *   </li>
 *   <li>Disables CSRF protection as the application uses stateless sessions.</li>
 * </ul>
 * </p>
 * 
 * <p>Dependencies:
 * <ul>
 *   <li>{@link JwtAuthenticationConverter} for converting JWT tokens into authentication objects.</li>
 *   <li>An array of admin authorities injected via the {@code adminAuthorities} qualifier.</li>
 * </ul>
 * </p>
 * 
 * @see org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
 * @see org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
 * @see org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
 * @see org.springframework.security.config.http.SessionCreationPolicy
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationConverter jwtAuthenticationConverter;
    private final String[] adminAuthorities;

    public SecurityConfig(JwtAuthenticationConverter jwtAuthenticationConverter, @Qualifier("adminAuthorities") String[] adminAuthorities) {
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
        this.adminAuthorities = adminAuthorities;
    }
   
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/private/**").authenticated()
                .requestMatchers("/api/admin/**").hasAnyAuthority(this.adminAuthorities)
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(this.jwtAuthenticationConverter)
                )
            );

        return http.build();
    }
}
