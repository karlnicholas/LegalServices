package com.github.karlnicholas.legalservices.slipopinion.processor;

import org.apache.kafka.clients.producer.Producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.karlnicholas.legalservices.caselist.model.CASELISTSTATUS;
import com.github.karlnicholas.legalservices.opinionview.model.OpinionView;

public class NewCaseListEntryProcessorComponent extends AbstractCaseListEntryProcessorComponent {
	public NewCaseListEntryProcessorComponent(ObjectMapper objectMapper, 
			KakfaProperties kafkaProperties, 
			Producer<Integer, OpinionView> producer
	) {
		super(objectMapper, kafkaProperties, producer, 
			caseListEntry->{
			caseListEntry.setStatus(CASELISTSTATUS.RETRY);
			caseListEntry.setRetryCount(1);
		});
	}
}
