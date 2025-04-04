package com.example.oktaapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

/**
 * <p>Configuration class for defining application roles and their associated authorities (which
 * for this app correspond to Okta groups).</p>
 * 
 * <p>This class binds properties prefixed with "app.roles" from the application's configuration
 * and provides a bean for each role that holds the associated authorities (i.e., Okta groups).</p>
 *
 * <p>Note: Ensure that getters and setters are present for property binding to work correctly.</p>
 */
@Configuration
@ConfigurationProperties(prefix = "app.roles")
public class AppAuthoritiesConfig {
    private List<String> user;
    private List<String> admin;

    @Bean
    public String[] userAuthorities() {
        return user.toArray(new String[0]);
    }

    @Bean
    public String[] adminAuthorities() {
        return admin.toArray(new String[0]);
    }

    // Getters and setters required for property binding
    public List<String> getUser() {
        return user;
    }

    public void setUser(List<String> user) {
        this.user = user;
    }

    public List<String> getAdmin() {
        return admin;
    }

    public void setAdmin(List<String> admin) {
        this.admin = admin;
    }
}