package com.audition.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //.loginPage("/login") // Custom login page
        http
            .csrf().disable() // Disable CSRF for simplicity; enable in production
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/actuator/info", "/actuator/health")
                .permitAll() // Allow public access to info and health
                .requestMatchers("/actuator/**").authenticated() // Require authentication for other actuator endpoints
                .anyRequest().authenticated() // All other requests need authentication
            )
            .formLogin(AbstractAuthenticationFilterConfigurer::permitAll
            )
            .logout(LogoutConfigurer::permitAll
            );

        return http.build();
    }

}
