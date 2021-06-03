package com.github.karlnicholas.legalservices.user.security.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.github.karlnicholas.legalservices.user.mailer.SendGridMailer;
import com.github.karlnicholas.legalservices.user.model.ApplicationUser;

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

//	/**
//     * Merge user with Database
//     * @param ApplicationUser to merge.
//     */
////    @Asynchronous
//    public void startVerify(ApplicationUser ApplicationUser) {
//		// prevent all exceptions from leaving @Asynchronous block
//    	try {
//	    	sendGridMailer.sendEmail(ApplicationUser, "/xsl/verify.xsl");
//	    	ApplicationUser.setStartVerify(true);
//	    	userService.merge(ApplicationUser);
//	    	logger.info("Verification started: " + ApplicationUser.getEmail());
//    	} catch ( Exception ex ) {
//	    	logger.error("Verification failed: {}", ex.getMessage());
//    	}
//    }

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

//	public void sendWelcomeEmail(ApplicationUser ApplicationUser) {
//    	ApplicationUser.setWelcomed(true);
//    	userService.merge(ApplicationUser);
//		sendGridMailer.sendEmail(ApplicationUser, "/xsl/welcome.xsl");
//    }

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
//        List<ApplicationUser> users = userService.findAllUnWelcomed();
//        for ( ApplicationUser user: users ) {
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
//        List<ApplicationUser> users = userService.findAll();
//        for ( ApplicationUser user: users ) {
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
        List<ApplicationUser> Users = userService.findAll();
        for ( ApplicationUser ApplicationUser: Users ) {
        	if ( ApplicationUser.isAdmin() ) {
	            // Prepare the evaluation context
        		sendGridMailer.sendSystemReport(ApplicationUser, memoryMap);
	            logger.info("System Report sent: " + ApplicationUser.getEmail());
	            //            System.out.println("Resend = " + account.getEmail());
        	}
        }
        logger.info("System Report completed");
	}


}