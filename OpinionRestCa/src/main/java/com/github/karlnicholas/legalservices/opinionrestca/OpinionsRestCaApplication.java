package com.github.karlnicholas.legalservices.opinionrestca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication(scanBasePackages = { "com.github.karlnicholas.legalservices"})
@Configuration
public class OpinionsRestCaApplication {
	public static void main(String[] args) {
		SpringApplication.run(OpinionsRestCaApplication.class, args);
	}

}
