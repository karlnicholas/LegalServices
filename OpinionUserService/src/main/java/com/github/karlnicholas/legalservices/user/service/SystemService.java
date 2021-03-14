package com.github.karlnicholas.legalservices.user.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.github.karlnicholas.legalservices.user.mailer.SendGridMailer;
import com.github.karlnicholas.legalservices.user.security.model.User;

//@Stateless
@Service
public class SystemService {
	private final UserService userService;
	private final SendGridMailer sendGridMailer;
    private Logger logger = LoggerFactory.getLogger(SystemService.class);
    public SystemService(UserService userService, SendGridMailer sendGridMailer) {
		super();
		this.userService = userService;
		this.sendGridMailer = sendGridMailer;
	}

	/**
     * Merge user with Database
     * @param user to merge.
     */
//    @Asynchronous
    public void startVerify(User user) {
		// prevent all exceptions from leaving @Asynchronous block
    	try {
	    	sendGridMailer.sendEmail(user, "/xsl/verify.xsl");
	    	user.setStartVerify(true);
	    	userService.merge(user);
	    	logger.info("Verification started: " + user.getEmail());
    	} catch ( Exception ex ) {
	    	logger.error("Verification failed: {}", ex.getMessage());
    	}
    }

    /**
     * Send email async
     * @param email from about
     * @param comment from about 
     * @param locale from about
     */
    @Async
	public void sendAboutEmail(String email, String comment, Locale locale) {
		// prevent all exceptions from leaving @Asynchronous block
    	try {
	    	sendGridMailer.sendComment(email, comment, locale);
	    	logger.info("Comment sent" );
		} catch ( Exception ex ) {
	    	logger.error("Comment send failed: {}", ex.getMessage());
		}
    }

	public void sendWelcomeEmail(User user) {
    	user.setWelcomed(true);
    	userService.merge(user);
		sendGridMailer.sendEmail(user, "/xsl/welcome.xsl");
    }

	public void doWelcomeService() {
//        logger.info("Welcome service started");
//        // So, do the real work.
//        // / accountRepository.findUnverified
//        Calendar cal = Calendar.getInstance();
//        int year = cal.get(Calendar.YEAR);
//        int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
//        dayOfYear = dayOfYear - 4;
//        if ( dayOfYear < 1 ) {
//            year = year - 1;
//            dayOfYear = 365 + dayOfYear;
//        }
//        cal.set(Calendar.YEAR, year);
//        cal.set(Calendar.DAY_OF_YEAR, dayOfYear);
//        Date threeDaysAgo = cal.getTime();
//
//        List<User> users = userService.findAllUnWelcomed();
//        for ( User user: users ) {
//        	if ( !user.isAdmin() ) {
//	            if ( user.getCreateDate().compareTo(threeDaysAgo) < 0 ) {
//	            		userService.delete(user.getId());
//	            } else {
//		            // Prepare the evaluation context
//		            sendWelcomeEmail(user);
//		            logger.info("Welcome email sent: " + user.getEmail());
//		            //            System.out.println("Resend = " + account.getEmail());
//	            }
//        	}
//        }
//            
////        String htmlContent = mailTemplateEngine.process("verify.html", ctx);
//        logger.info("Welcome service completed");
	}

	public void sendOpinionReports() {
//        logger.info("Opinion Reports started");
//        Calendar calNow = Calendar.getInstance();
//        Calendar calLastWeek = Calendar.getInstance();
//        int year = calLastWeek.get(Calendar.YEAR);
//        int dayOfYear = calLastWeek.get(Calendar.DAY_OF_YEAR);
//        dayOfYear = dayOfYear - 7;
//        if ( dayOfYear < 1 ) {
//            year = year - 1;
//            dayOfYear = 365 + dayOfYear;
//        }
//        calLastWeek.set(Calendar.YEAR, year);
//        calLastWeek.set(Calendar.DAY_OF_YEAR, dayOfYear);
//
//        List<User> users = userService.findAll();
//        for ( User user: users ) {
//        	if ( !user.isOptout() || user.isAdmin() ) {
//	            // Prepare the evaluation context
//        		ViewParameters viewInfo= new ViewParameters(calLastWeek.getTime(), calNow.getTime());
//        		List<OpinionView> opinionViews;
//        		if ( user.isAdmin() ) {
//            		opinionViews = opinionViewSingleton.getOpinionCases(viewInfo);
//        		} else {
//            		opinionViews = opinionViewSingleton.getOpinionCasesForAccount(viewInfo, user);
//        		}
//        		if ( opinionViews.size() > 0 ) {
//	        		sendGridMailer.sendOpinionReport(user, opinionViews);
//		            logger.info("Case Report sent: " + user.getEmail());
//        		}
//	            //            System.out.println("Resend = " + account.getEmail());
//        	}
//        }
//        logger.info("Opinion Reports completed");
	}

	public void sendSystemReport(Map<String, Long> memoryMap) {
        logger.info("System Report started");
        List<User> users = userService.findAll();
        for ( User user: users ) {
        	if ( user.isAdmin() ) {
	            // Prepare the evaluation context
        		sendGridMailer.sendSystemReport(user, memoryMap);
	            logger.info("System Report sent: " + user.getEmail());
	            //            System.out.println("Resend = " + account.getEmail());
        	}
        }
        logger.info("System Report completed");
	}


}