package com.example.oktaapi.controller;

import com.example.oktaapi.config.AppAuthoritiesConfig;
import com.example.oktaapi.model.Message;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A controller class that defines secured endpoints for the application.
 */
@RestController
public class SecuredController {

    private static final Logger logger = LoggerFactory.getLogger(SecuredController.class);

    /**
     * Handles GET requests to the "/api/private" endpoint.
     * This endpoint is secured and requires authentication.
     * <p>
     * Authorization is based on a JWT bearer token provided in the Authorization
     * header. Token requirements:
     * </p>
     * <ul>
     *   <li>Must have the scope 'data.read'.</li>
     *   <li>Additionally, one of two conditions must be met:
     *     <ul>
     *       <li>The token must be a client credentials token as determined by
     *           the '@jwtUtils.isClientCredentials()' method.</li>
     *       <li>OR the caller must have one of the authorities defined in the '@userAuthorities'
     *           bean, which holds an array of the Okta group(s) that are bound to the logical
     *           'user' role in the application. See the {@link AppAuthoritiesConfig} class and the
     *           application.yml config file for more details on how these authorities are
     *           configured.</li>
     *     </ul>
     *   </li>
     * </ul>
     * 
     * @param jwt the JWT token of the authenticated user, injected by Spring Security.
     * @return a {@link Message} object containing a simple message.
     */
    @GetMapping("/api/private")
    @PreAuthorize("hasAuthority('SCOPE_data.read') and (hasAnyAuthority(@userAuthorities) or @jwtUtils.isClientCredentials())")
    public Message getPrivateMessage(@AuthenticationPrincipal Jwt jwt) {
        if (logger.isDebugEnabled()) {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.getAuthentication().getAuthorities().forEach(
                authority -> logger.debug("Authority: " + authority.getAuthority())
            );
        }

        return new Message("This is a private endpoint that requires authentication");
    }

    /**
     * Handles GET requests to the "/api/admin" endpoint.
     * This endpoint is secured and requires an authenticated admin user.
     * <p>
     * For this endpoint, there is no @PreAuthorize annotation, but the SecurityConfig
     * class specifies that requests to "/api/admin/**" must have one of the authorities
     * defined in the {@code @adminAuthorities} bean. Like the {@code @userAuthorities} bean, 
     * this bean is an array of strings representing the Okta group(s) that are granted admin
     * authority. These groups are listed in the {@code app.roles.admin} property in the
     * application configuration file (application.yml).
     * </p>
     * <p>
     * Client credentials tokens are not supported by this endpoint.
     * </p>
     *
     * @param jwt the JSON Web Token (JWT) representing the authenticated user,
     *            injected via the @AuthenticationPrincipal annotation.
     * @return a {@link Message} object containing a message indicating that this
     *         is an admin-only endpoint.
     */
    @GetMapping("/api/admin")
    public Message getAdminMessage(@AuthenticationPrincipal Jwt jwt) {
        return new Message("This is an admin endpoint that requires an admin user");
    }

    /**
     * Retrieves token information from the Okta JWT bearer token found in the request's
     * Authorization header.
     * <p>
     * No particular authorities are required, but a valid Okta token is expected. Supports
     * tokens generated with both the Authorization Code flow and Client Credentials flow.
     * </p>
     * 
     * @param jwt the JSON Web Token (JWT) containing user claims, injected via @AuthenticationPrincipal
     * @return a map containing token information such as subject, name, email, and all claims
     */
    @GetMapping("/api/token-info")
    public Map<String, Object> getTokenInfo(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> tokenInfo = new HashMap<>();
        
        // Extract claims from the JWT
        String subject = jwt.getSubject();
        String name = jwt.getClaimAsString("name");
        String email = jwt.getClaimAsString("email");
        
        tokenInfo.put("subject", subject);
        tokenInfo.put("name", name);
        tokenInfo.put("email", email);
        tokenInfo.put("claims", jwt.getClaims());
        
        return tokenInfo;
    }
}
