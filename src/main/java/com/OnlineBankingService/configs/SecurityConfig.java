package com.OnlineBankingService.configs;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.security.Security;

@Configuration
public class SecurityConfig {

    @PostConstruct
    public void init() {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }
}
