package com.OnlineBankingService.generator;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class AccountNameGenerator {

    private final SecureRandom random = new SecureRandom();

    public String generate16Digits() {
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
