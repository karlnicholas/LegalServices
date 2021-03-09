package com.github.karlnicholas.opinionservices.slipopinion.processor;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.karlnicholas.opinionservices.slipopinion.dao.OpinionViewDao;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@SpringBootApplication(scanBasePackages = {"opca", "com.github.karlnicholas.opinionservices.slipopinion.processor"})
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
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        config.setJdbcUrl( "jdbc:mysql://localhost:3306/op" );
        config.setUsername( "op" );
        config.setPassword( "op" );
        
        config.addDataSourceProperty( "maximumPoolSize" , "10" );
        config.addDataSourceProperty( "minimumIdle",  "2" );
        config.addDataSourceProperty( "useServerPrepStmts", "true" );
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "256" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        ds = new HikariDataSource( config );
    }

    @Bean
    OpinionViewDao getOpinionViewDao() throws SQLException {
		return new OpinionViewDao(ds);
	}
}