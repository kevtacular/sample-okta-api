package com.example.oktaapi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.oktaapi.config.AppAuthoritiesConfig;
import com.example.oktaapi.config.TestSecurityConfig;

@WebMvcTest(controllers = SecuredController.class)
@Import({
    AppAuthoritiesConfig.class, 
    TestSecurityConfig.class
})
@ActiveProfiles("test")
class SecuredControllerTest {

    private static final String TEST_SCOPE = "data.read";
    private static final String SCOPE_AUTHORITY = "SCOPE_" + TEST_SCOPE;
    private static final String TEST_USER_GROUP = "TestUserGroup";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;
    
    @Nested
    class AuthorizationCodeFlowTests {

        @Test
        void whenUserHasRequiredGroupAndScope_thenAllowAccess() throws Exception {
            // Create a JWT with required scopes and groups
            Jwt jwt = createUserJwtToken(
                List.of(TEST_SCOPE),
                List.of(TEST_USER_GROUP)
            );

            // Perform GET request to /api/private with the JWT token
            mockMvc.perform(get("/api/private")
                    .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt)
                        .authorities(
                            new SimpleGrantedAuthority(SCOPE_AUTHORITY),
                            new SimpleGrantedAuthority(TEST_USER_GROUP)
                        )))
                    .andExpect(status().isOk());
        }

        @Test
        void whenTokenLacksRequiredScope_thenDisallowAccess() throws Exception {
            Jwt jwt = createUserJwtToken(
                List.of("other.scope"),
                List.of(SCOPE_AUTHORITY)
            );

            // Perform GET request to /api/private with the JWT token
            mockMvc.perform(get("/api/private")
                    .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt)
                        .authorities(
                            new SimpleGrantedAuthority("SCOPE_other.scope"), // This scope does not match SCOPE_DATA_READ
                            new SimpleGrantedAuthority(SCOPE_AUTHORITY)
                        )))
                    .andExpect(status().isForbidden());
        }

        @Test
        void whenUserLacksRequiredGroup_thenDisallowAccess() throws Exception {
            Jwt jwt = createUserJwtToken(
                List.of(TEST_SCOPE),
                List.of("Some Group", "Another Group") // This user does not have the required group
            );

            // Perform GET request to /api/private with the JWT token
            mockMvc.perform(get("/api/private")
                    .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt)
                        .authorities(
                            new SimpleGrantedAuthority(SCOPE_AUTHORITY),
                            new SimpleGrantedAuthority("Some Group"),
                            new SimpleGrantedAuthority("Another Group")
                        )))
                    .andExpect(status().isForbidden());
        }

        private Jwt createUserJwtToken(List<String> scopes, List<String> groups) {
            return Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("user123")  // Different from client ID
                .claim("cid", "client123")
                .claim("groups", groups)
                .claim("scp", scopes)
                .build();
        }
    }

    @Nested
    class ClientCredentialsFlowTests {
        @Test
        void whenClientHasRequiredScope_thenAllowAccess() throws Exception {
            Jwt jwt = createClientJwtToken(List.of(TEST_SCOPE));

            mockMvc.perform(get("/api/private")
                    .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt)
                        .authorities(
                            new SimpleGrantedAuthority(SCOPE_AUTHORITY)
                        )))
                    .andExpect(status().isOk());
        }

        @Test
        void whenClientLacksRequiredScope_thenDisallowAccess() throws Exception {
            Jwt jwt = createClientJwtToken(List.of("other.scope", "another.scope")); // Lacking required scope

            mockMvc.perform(get("/api/private")
                    .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt)
                        .authorities(
                            new SimpleGrantedAuthority("SCOPE_other.scope"),
                            new SimpleGrantedAuthority("SCOPE_another.scope")
                        )))
                    .andExpect(status().isForbidden());
        }

        private Jwt createClientJwtToken(List<String> scopes) {
            String clientId = "client123";
            return Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject(clientId)  // Subject equals client ID for client credentials
                .claim("cid", clientId)
                .claim("scp", scopes)
                .build();
        }
    }
}
