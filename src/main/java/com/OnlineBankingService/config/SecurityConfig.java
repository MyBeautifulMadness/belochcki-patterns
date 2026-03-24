package com.OnlineBankingService.config;

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

                        .requestMatchers("/api/employees/**").hasAuthority("SCOPE_EMPLOYEE")
                        .requestMatchers("/api/clients/**").hasAnyAuthority("SCOPE_CLIENT", "SCOPE_EMPLOYEE")



                        //Credit Tariff Controller
                        .requestMatchers("/api/creditTariff/update/**").hasAnyAuthority("SCOPE_EMPLOYEE")
                        .requestMatchers("/api/creditTariff/create").hasAnyAuthority("SCOPE_EMPLOYEE")
                        .requestMatchers("/api/creditTariff/getById/**").permitAll()
                        .requestMatchers("/api/creditTariff/getAll").permitAll()
                        .requestMatchers("/api/creditTariff/delete/**").hasAnyAuthority("SCOPE_EMPLOYEE")

                        //Client Credit Controller
                        .requestMatchers("/api/clientCredit/repay").hasAnyAuthority("SCOPE_CLIENT")
                        .requestMatchers("/api/clientCredit/create").hasAnyAuthority("SCOPE_CLIENT")
                        .requestMatchers("/api/clientCredit/getById/**").permitAll()
                        .requestMatchers("/api/clientCredit/getAll").permitAll()

                        //Credit Operation History Controller
                        .requestMatchers("/api/creditOperationHistory/getAll").permitAll()

                        //Client Credit Rating Controller
                        .requestMatchers("/api/clientDebts").permitAll()
                        .requestMatchers("/api/clientCreditRating").permitAll()



                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> {})
                );

        return http.build();
    }
}
