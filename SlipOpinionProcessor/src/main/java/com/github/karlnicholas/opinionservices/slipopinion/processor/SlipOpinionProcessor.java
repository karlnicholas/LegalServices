package com.github.karlnicholas.opinionservices.slipopinion.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication(scanBasePackages = {"opca", "com.github.karlnicholas.opinionservices.slipopinion.processor"})
@EnableScheduling
@EnableAsync
@Configuration
public class SlipOpinionProcessor {
	Logger logger = LoggerFactory.getLogger(SlipOpinionProcessor.class);
	public static void main(String... args) {
		new SpringApplicationBuilder(SlipOpinionProcessor.class).run(args);
	}

	@Autowired TaskExecutor taskExecutor;
	@Autowired ObjectMapper objectMapper;
	
	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
		taskExecutor.execute(new KafkaConsumerTask(objectMapper));
		taskExecutor.execute(new KafkaConsumerTask(objectMapper));
		taskExecutor.execute(new KafkaConsumerTask(objectMapper));
	}
	
}