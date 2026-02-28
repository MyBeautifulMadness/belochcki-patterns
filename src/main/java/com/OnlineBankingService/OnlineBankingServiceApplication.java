package com.OnlineBankingService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.OnlineBankingService.configs")
public class OnlineBankingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineBankingServiceApplication.class, args);
	}

}
