package com.github.karlnicholas.legalservices.statuterestca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.github.karlnicholas.legalservices"})
public class StatutesRestCaApplication {

	public static void main(String[] args) {
		SpringApplication.run(StatutesRestCaApplication.class, args);
	}
}
