package com.example.oktaapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p>The main entry point for the Sample Okta API application.</p>
 * 
 * <p>This class is annotated with {@code @SpringBootApplication}, which serves as a 
 * convenience annotation that adds all of the following:</p>
 * <ul>
 *   <li>{@code @Configuration}: Marks the class as a source of bean definitions.</li>
 *   <li>{@code @EnableAutoConfiguration}: Enables Spring Boot's auto-configuration mechanism.</li>
 *   <li>{@code @ComponentScan}: Enables component scanning for the package and its sub-packages.</li>
 * </ul>
 */
@SpringBootApplication
public class OktaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OktaApiApplication.class, args);
    }
}
