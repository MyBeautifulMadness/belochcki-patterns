package com.OnlineBankingService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.spec.SecretKeySpec;

@Configuration
public class JwtConfig {

    private static final String SECRET = "MySuperSecretKeyThatIsLongEnough123456";

    @Bean
    public NimbusJwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(
                new SecretKeySpec(SECRET.getBytes(), "HmacSHA256")
        ).build();
    }
}