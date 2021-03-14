package com.github.karlnicholas.legalservices.opinionview.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


//@TransactionManagement(TransactionManagementType.BEAN)
//@Singleton
@Service
public class ScraperScheduledService {
    private Logger logger = LoggerFactory.getLogger(ScraperScheduledService.class);
    private final CAOnlineUpdates caOnlineUpdates;
    private final OpinionViewSingleton opinionViewSingleton;

//    @Resource private EJBContext context;

public ScraperScheduledService(CAOnlineUpdates caOnlineUpdates, 
			OpinionViewSingleton opinionViewSingleton
	) {
		super();
		this.caOnlineUpdates = caOnlineUpdates;
		this.opinionViewSingleton = opinionViewSingleton;
	}

    // timeout issue.
    // @TransactionTimeout(value=1, unit = TimeUnit.HOURS)
    // this is handled in wildfly standalone.xml configuration file
    // though it is currently pretty fast, so maybe not needed.
	//    @Scheduled(second="0", minute="00", hour="18", dayOfWeek="Mon-Fri", persistent=false)        // 03:30 am (12:30 am AZ ) every day
//	@Scheduled(cron = "0 0 18 * * MON-FRI")
//    public void updateSlipOpinions() throws SQLException {
//        logger.info("STARTING updateSlipOpinions");
//        List<OpinionKey> opinionKeys = null;
//        StatutesService statutesService = StatutesServiceFactory.getStatutesServiceClient();
//        opinionKeys = caOnlineUpdates.updateDatabase(new TestCAParseSlipDetails(false), statutesService);
//        if ( opinionKeys != null ) {
//        	opinionViewSingleton.updateOpinionViews(opinionKeys, statutesService);
//        }
//        logger.info("DONE updateSlipOpinions");
//    }

}