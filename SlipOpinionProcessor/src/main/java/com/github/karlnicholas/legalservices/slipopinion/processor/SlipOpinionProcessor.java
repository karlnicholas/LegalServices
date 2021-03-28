package com.github.karlnicholas.legalservices.slipopinion.processor;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication(scanBasePackages = {
	"com.github.karlnicholas.legalservices.opinionview.service", 
	"com.github.karlnicholas.legalservices.slipopinion.processor", 
	"com.github.karlnicholas.legalservices.opinionview.controller"
})
@EnableScheduling
@EnableAsync
public class SlipOpinionProcessor {
	Logger logger = LoggerFactory.getLogger(SlipOpinionProcessor.class);
	public static void main(String... args) {
		new SpringApplicationBuilder(SlipOpinionProcessor.class).run(args);
	}

	@Autowired TaskExecutor taskExecutor;
	@Autowired ObjectMapper objectMapper;
	@Autowired KakfaProperties kakfaProperties;
	@Autowired OpinionViewData opinionViewData;
	
	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() throws SQLException {

		taskExecutor.execute(new OpinionViewCacheComponent(kakfaProperties, opinionViewData));

		taskExecutor.execute(new OpinionViewBuildComponent(objectMapper, kakfaProperties));
		taskExecutor.execute(new OpinionViewBuildComponent(objectMapper, kakfaProperties));
		taskExecutor.execute(new OpinionViewBuildComponent(objectMapper, kakfaProperties));
	}
//	@Bean
//	public WebMvcConfigurer configurer() {
//		return new WebMvcConfigurer() {
//			@Override
//			public void addCorsMappings(CorsRegistry registry) {
//				registry.addMapping("/**").allowedOrigins("http://localhost:3000");
//			}
//		};
//	}
}