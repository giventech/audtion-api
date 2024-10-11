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
@SuppressWarnings("PMD")
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        http
            .csrf().disable() // Disable CSRF for simplicity; enable in production
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/actuator/info", "/actuator/health")
                .permitAll() // Allow public access to info and health
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui/index.html")
                .permitAll() // Allow access to Swagger UI
                .requestMatchers("/posts/**")
                .permitAll() // Allow access to Swagger UI
                .requestMatchers("/actuator/**").authenticated()

                .anyRequest().authenticated() // All other requests need authentication
            )
            .formLogin(AbstractAuthenticationFilterConfigurer::permitAll
            )
            .logout(LogoutConfigurer::permitAll
            );

        return http.build();
    }

}
