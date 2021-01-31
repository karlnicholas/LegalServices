package opinionsweb.controller;

import java.util.Collections;
import java.util.Date;
import java.util.List;

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

	@GetMapping(value = "opinions", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<OpinionView> getOpinionViewList(@RequestParam(name = "startDate", required = false) String startDate) {
		if ( opinionViewSingleton.getReportDates() != null ) {
			// done this way so that this information is not serialized in the viewScope
			if (opinionViewSingleton.getReportDates().size() == 0) {
				return null;
			}
			int currentIndex = 0;
	    	if ( startDate != null ) {
	        	currentIndex = opinionViewSingleton.currentDateIndex(startDate);
	    	}
	    	Date[] dates = opinionViewSingleton.getReportDates().get(currentIndex);
			ViewParameters viewInfo = new ViewParameters(dates[0], dates[1]);
			return opinionViewSingleton.getOpinionCases(viewInfo);
		} else {
			return Collections.emptyList();
		}
	}

	@GetMapping(value = "dates", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String[]> getDateList() {
		return opinionViewSingleton.getStringDateList();
	}

	@GetMapping(value = "status", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean checkStatus() {
		return opinionViewSingleton.checkStatus();
	}
}