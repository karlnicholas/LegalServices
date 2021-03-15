package com.github.karlnicholas.legalservices.opinionrestca;

import java.sql.SQLException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@SpringBootApplication(scanBasePackages = {"com.github.karlnicholas.legalservices.opinionrestca", "opca"})
public class OpinionsRestCaApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpinionsRestCaApplication.class, args);
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
	OpinionBaseDao getOPinionBaseDao() throws SQLException {
		return new OpinionBaseDao(ds);
	}
}
