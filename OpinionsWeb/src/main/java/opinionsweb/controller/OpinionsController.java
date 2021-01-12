package opinionsweb.controller;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import opca.service.OpinionViewSingleton;
import opca.service.ViewParameters;
import opca.view.OpinionView;

@RestController
@RequestMapping("api/opinions")
public class OpinionsController {
	private final OpinionViewSingleton opinionViewSingleton;

    public OpinionsController(OpinionViewSingleton opinionViewSingleton) {
		super();
		this.opinionViewSingleton = opinionViewSingleton;
	}

//    @PostConstruct
//    public void postConstruct() {
//    	opinionViewSingleton.checkStatus();
//    }
    @GetMapping(value="list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OpinionView> getOpinionViewList(@RequestParam("dateListIndex") String dateListIndex) {
		// done this way so that this information is not serialized in the viewScope
    	if ( opinionViewSingleton.getReportDates().size() == 0 ) {
    		return null;
    	}
    	Date[] dates = opinionViewSingleton.getReportDates().get(Integer.parseInt(dateListIndex));
		ViewParameters viewInfo = new ViewParameters(dates[0],dates[1]);
		return opinionViewSingleton.getOpinionCases(viewInfo);
	}
    @GetMapping(value="dates", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String[]> getDateList() {
		return opinionViewSingleton.getStringDateList();
	}
    @GetMapping(value="ready", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean isCacheReady() {
		return opinionViewSingleton.isReady();
	}
    @GetMapping(value="load")
	public boolean loadCache() {
		return opinionViewSingleton.checkStatus();
	}
}