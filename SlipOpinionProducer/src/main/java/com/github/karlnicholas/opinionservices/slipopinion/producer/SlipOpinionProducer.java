package com.github.karlnicholas.opinionservices.slipopinion.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"opca", "com.github.karlnicholas.opinionservices.slipopinion.producer"})
@EnableScheduling
public class SlipOpinionProducer {
	Logger logger = LoggerFactory.getLogger(SlipOpinionProducer.class);
	public static void main(String... args) {
		new SpringApplicationBuilder(SlipOpinionProducer.class).run(args);
	}

}