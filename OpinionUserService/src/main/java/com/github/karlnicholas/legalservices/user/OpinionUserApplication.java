package com.github.karlnicholas.legalservices.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.karlnicholas.legalservices.opinionview.kafka.KakfaProperties;
import com.github.karlnicholas.legalservices.opinionview.kafka.OpinionViewData;
import com.github.karlnicholas.legalservices.user.kafka.EmailOpinionsComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.SQLException;

//@SpringBootApplication(scanBasePackages = {
//	"com.github.karlnicholas.legalservices.opinionview.service", 
//	"com.github.karlnicholas.legalservices.slipopinion.processor", 
//	"com.github.karlnicholas.legalservices.opinionview.controller", 
//	"com.github.karlnicholas.legalservices.sqlutil"
//})

@SpringBootApplication(scanBasePackages = {"com.github.karlnicholas.legalservices"})
@EnableScheduling
public class OpinionUserApplication {
	Logger logger = LoggerFactory.getLogger(OpinionUserApplication.class);
	public static void main(String... args) {
		new SpringApplicationBuilder(OpinionUserApplication.class).run(args);
	}

	@Autowired
	private KakfaProperties kafkaProperties;
	@Autowired
	private OpinionViewData opinionViewData;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private DataSource dataSource;

	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() throws SQLException {
		new Thread(new EmailOpinionsComponent(kafkaProperties, opinionViewData, objectMapper, dataSource)).start();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}