package com.github.karlnicholas.legalservices.slipopinion.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.karlnicholas.legalservices.caselist.model.CASELISTSTATUS;
import com.github.karlnicholas.legalservices.caselist.model.CaseListEntry;

public class RetryCaseListEntryProcessorComponent extends AbstractCaseListEntryProcessorComponent {
	private RetryCaseListEntryProcessorComponent(ObjectMapper objectMapper, 
			KakfaProperties kafkaProperties, 
			java.util.function.Consumer<CaseListEntry> errorConsumer
	) {
		super(objectMapper, kafkaProperties, caseListEntry->{
			caseListEntry.setStatus(CASELISTSTATUS.ERROR);
			caseListEntry.setRetryCount(caseListEntry.getRetryCount()+1);
		});
	}
}
