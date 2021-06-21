package com.github.karlnicholas.legalservices.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

//@SpringBootApplication(scanBasePackages = {
//	"com.github.karlnicholas.legalservices.opinionview.service", 
//	"com.github.karlnicholas.legalservices.slipopinion.processor", 
//	"com.github.karlnicholas.legalservices.opinionview.controller", 
//	"com.github.karlnicholas.legalservices.sqlutil"
//})

@SpringBootApplication(scanBasePackages = {"com.github.karlnicholas.legalservices"}) 
public class OpinionUserApplication {
	Logger logger = LoggerFactory.getLogger(OpinionUserApplication.class);
	public static void main(String... args) {
		new SpringApplicationBuilder(OpinionUserApplication.class).run(args);
	}

}