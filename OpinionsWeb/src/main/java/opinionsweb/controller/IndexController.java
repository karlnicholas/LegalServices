package opinionsweb.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import opca.service.ScheduledService;

@RestController
@RequestMapping("api/home")
public class IndexController {
	Logger logger = LoggerFactory.getLogger(IndexController.class);
    private String userCountMessage;
    private final ScheduledService scheduledService; 
    
    public IndexController(ScheduledService scheduledService) {
		super();
		this.scheduledService = scheduledService;
	}

	public void testUpdate() {
    	scheduledService.updateSlipOpinions();
    }
    
    public void testWelcome() {
    	scheduledService.welcomingService();;
    }

    public void testOpinionReport() {
    	scheduledService.opinionReport();
    }

    public void testSystemReport() {
    	scheduledService.systemReport();
    }

    public void testNothing() {
        logger.info("Test Nothing");
    }

    private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String displayText(){
		return "";
	}

    /**
     * UserCountMessage field
     * @return userCountMessage
     */
    public String getUserCountMessage() {
        return userCountMessage;
    }

    /**
     * UserCountMessage Field
     * @param userCountMessage to set.
     */
    public void setUserCountMessage(String userCountMessage) {
        this.userCountMessage = userCountMessage;
    }
}