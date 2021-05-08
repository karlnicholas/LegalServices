package com.github.karlnicholas.legalservices.slipopinion.processor;

import org.apache.kafka.clients.producer.Producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.karlnicholas.legalservices.caselist.model.CASELISTSTATUS;
import com.github.karlnicholas.legalservices.opinionview.model.OpinionView;

public class RetryCaseListEntryProcessorComponent extends AbstractCaseListEntryProcessorComponent {
	public RetryCaseListEntryProcessorComponent(ObjectMapper objectMapper, 
			KakfaProperties kafkaProperties, 
			Producer<Integer, OpinionView> producer
	) {
		super(objectMapper, kafkaProperties, producer, caseListEntry->{
			caseListEntry.setRetryCount(caseListEntry.getRetryCount()+1);
			if ( caseListEntry.getRetryCount() >= 3 ) {
				caseListEntry.setStatus(CASELISTSTATUS.FAILED);
			} else {
				caseListEntry.setStatus(CASELISTSTATUS.RETRY);
			}
		});
	}
}
