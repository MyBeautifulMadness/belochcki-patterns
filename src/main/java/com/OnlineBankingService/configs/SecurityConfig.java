package com.OnlineBankingService.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/employees/test").permitAll()
                        .requestMatchers("/api/clients/token").hasAnyAuthority("SCOPE_CLIENT", "SCOPE_EMPLOYEE")
                        .requestMatchers("/api/employees/**").hasAuthority("SCOPE_EMPLOYEE")
                        .requestMatchers("/api/clients/**").hasAnyAuthority("SCOPE_CLIENT", "SCOPE_EMPLOYEE")

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> {})
                );

        return http.build();
    }
}
