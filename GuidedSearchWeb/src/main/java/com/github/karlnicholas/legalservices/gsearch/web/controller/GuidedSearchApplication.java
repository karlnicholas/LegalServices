package com.github.karlnicholas.legalservices.gsearch.web.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.github.karlnicholas.legalservices.gsearch.web.controller"})
public class GuidedSearchApplication {
	public static void main(String[] args) {
		SpringApplication.run(GuidedSearchApplication.class, args);
	}
}

