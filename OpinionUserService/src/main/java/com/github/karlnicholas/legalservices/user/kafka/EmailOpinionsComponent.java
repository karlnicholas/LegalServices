package com.github.karlnicholas.legalservices.user.kafka;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import com.github.karlnicholas.legalservices.opinionview.dao.SlipOpininScraperDao;
import com.github.karlnicholas.legalservices.opinionview.model.OpinionView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

public class EmailOpinionsComponent {
    private final Logger log = LoggerFactory.getLogger(EmailOpinionsComponent.class);
    private final DataSource dataSource;
    private final SlipOpininScraperDao slipOpininScraperDao;
    private final RestTemplate restTemplate;

    public EmailOpinionsComponent(
            ObjectMapper objectMapper,
            DataSource dataSource,
            RestTemplateBuilder builder
    ) {
        this.dataSource = dataSource;
        slipOpininScraperDao = new SlipOpininScraperDao();
        this.restTemplate = builder.rootUri("http://localhost:8080").build();
    }

    @Scheduled(fixedRate = 86400000, initialDelay = 60000)
    public String run() throws SQLException {
        try ( Connection con = dataSource.getConnection()) {
            String emailUserNeeded = slipOpininScraperDao.callEmailUserNeeded(con);
            if ( emailUserNeeded != null && emailUserNeeded.equalsIgnoreCase("NOEMAIL")) {
                return "NOEMAIL";
            }
        }
        int dayOfWeek = LocalDate.now().getDayOfWeek().getValue() + 1;
        int minusDays = 7 + dayOfWeek % 7;
        LocalDate pastDate = LocalDate.now().minusDays(minusDays);
        Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put("startDate", pastDate.toString());
        ResponseEntity<OpinionView[]> entity = restTemplate.getForEntity("/api/opinionviews/cases/{startDate}",
                OpinionView[].class,
                urlParameters);
        for ( OpinionView opinionView: entity.getBody()) {
        }
        return "EMAIL";
    }
}
