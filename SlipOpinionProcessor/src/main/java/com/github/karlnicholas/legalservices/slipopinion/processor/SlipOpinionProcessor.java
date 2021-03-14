package com.github.karlnicholas.legalservices.slipopinion.processor;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication(scanBasePackages = {"opca", "com.github.karlnicholas.opinionservices.slipopinion.processor", "com.github.karlnicholas.opinionservices.opinionview.controller"})
@EnableScheduling
@EnableAsync
@EnableConfigurationProperties(KakfaProperties.class)
public class SlipOpinionProcessor {
	Logger logger = LoggerFactory.getLogger(SlipOpinionProcessor.class);
	public static void main(String... args) {
		new SpringApplicationBuilder(SlipOpinionProcessor.class).run(args);
	}

	@Autowired TaskExecutor taskExecutor;
	@Autowired ObjectMapper objectMapper;
	@Autowired KakfaProperties kakfaProperties;
	@Autowired OpinionViewCache opinionViewCache;
	
	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() throws SQLException {

		taskExecutor.execute(new OpinionViewCacheComponent(kakfaProperties, opinionViewCache));

		taskExecutor.execute(new OpinionViewBuildComponent(objectMapper, kakfaProperties));
		taskExecutor.execute(new OpinionViewBuildComponent(objectMapper, kakfaProperties));
		taskExecutor.execute(new OpinionViewBuildComponent(objectMapper, kakfaProperties));
	}
	@Bean
	public WebMvcConfigurer configurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("http://localhost:3000");
			}
		};
	}
}