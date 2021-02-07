package opinionsweb.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import opca.service.OpinionViewSingleton;
import opca.service.ScheduledService;

@RestController
@RequestMapping("api/home")
public class IndexController {
	Logger logger = LoggerFactory.getLogger(IndexController.class);
    private String userCountMessage;
    private final ScheduledService scheduledService; 
	private final OpinionViewSingleton opinionViewSingleton;

    public IndexController(ScheduledService scheduledService, 
    		OpinionViewSingleton opinionViewSingleton
	) {
		super();
		this.scheduledService = scheduledService;
		this.opinionViewSingleton = opinionViewSingleton;
	}

    @GetMapping(value="ready", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean isCacheReady() {
		return opinionViewSingleton.isReady();
	}
    @GetMapping(value="load", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean loadCache() {
		return opinionViewSingleton.checkStatus();
	}
    
    @GetMapping(value="testUpdate", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean testUpdate() {
    	scheduledService.updateSlipOpinions();
    	return Boolean.TRUE;
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