package com.github.karlnicholas.legalservices.opinionrestca;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@SpringBootApplication(scanBasePackages = { "com.github.karlnicholas.legalservices.opinionrestca"})
@ConfigurationProperties("com.github.karlnicholas.legalservices.opinionrestca")
@Configuration
public class OpinionsRestCaApplication {
	@Value("${database-url:mysql://localhost:3306/}")
    private String databaseUrl;
	@Value("${database-name:op}")
	private String databaseName;
	@Value("${database-user:op}")
	private String databaseUser;
	@Value("${database-password:op}")
	private String databasePassword;

	@Value("${maximumPoolSize:10}")
	private String maximumPoolSize;
	@Value("${minimumIdle:2}")
	private String minimumIdle;
	@Value("${useServerPrepStmts:true}")
	private String useServerPrepStmts;
	@Value("${cachePrepStmts:true}")
	private String cachePrepStmts;
	@Value("${prepStmtCacheSize:256}")
	private String prepStmtCacheSize;
	@Value("${prepStmtCacheSqlLimit:2048}")
	private String prepStmtCacheSqlLimit;

	public static void main(String[] args) {
		SpringApplication.run(OpinionsRestCaApplication.class, args);
	}

	@Bean
	public DataSource getDataSource() throws SQLException {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:" + databaseUrl + databaseName);
		config.setUsername(databaseUser);
		config.setPassword(databasePassword);

		config.addDataSourceProperty("maximumPoolSize", maximumPoolSize);
		config.addDataSourceProperty("minimumIdle", minimumIdle);
		config.addDataSourceProperty("useServerPrepStmts", useServerPrepStmts);
		config.addDataSourceProperty("cachePrepStmts", cachePrepStmts);
		config.addDataSourceProperty("prepStmtCacheSize", prepStmtCacheSize);
		config.addDataSourceProperty("prepStmtCacheSqlLimit", prepStmtCacheSqlLimit);
		return new HikariDataSource(config);
	}

	@Bean
	OpinionBaseDao getOPinionBaseDao() throws SQLException {
		return new OpinionBaseDao(getDataSource());
	}
}
