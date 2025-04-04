package com.example.oktaapi.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility Spring bean for handling JWT (JSON Web Token) related operations.
 * 
 * <p>This class provides methods to interact with JWT tokens, such as determining
 * if the current authentication token represents a client credentials flow.</p>
 * 
 * <p>It relies on Spring Security's {@link SecurityContextHolder} to access the
 * current authentication context and extract JWT details.</p>
 * 
 * <p>The bean is useful in method-level security annotations (e.g., {@code @PreAuthorize})
 * to implement custom security logic based on JWT claims. For example, it can be used
 * in a Spring expression to check if the current token is from a client credentials flow:</p>
 * 
 * <pre>
 * {@code
 * @PreAuthorize("hasAuthority('SCOPE_data.read') and (hasAnyAuthority(@userAuthorities) or @jwtUtils.isClientCredentials())")
 * }
 * </pre>
 */
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    
    /**
     * Determines if the current authentication token represents a client credentials flow.
     * 
     * <p>This method checks the authentication object from the security context to determine
     * if it is a JWT token. If it is, it compares the token's subject with the client ID
     * ("cid" claim) to identify if the token was issued using a client credentials grant type.
     * 
     * @return {@code true} if the token was geneated using a client credentials grant type, {@code false} otherwise.
     */
    public boolean isClientCredentials() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String subject = jwt.getSubject();
            String clientId = jwt.getClaimAsString("cid");
            
            boolean isClientCredentials = subject != null && subject.equals(clientId);
            logger.debug("Token type: {}", isClientCredentials ? "client_credentials" : "user");
            return isClientCredentials;
        }
        return false;
    }
}