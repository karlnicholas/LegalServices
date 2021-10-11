package com.github.karlnicholas.legalservices.opinionview.model;

import java.io.Serializable;
import java.util.Optional;

import com.github.karlnicholas.legalservices.caselist.model.CaseListEntry;
import com.github.karlnicholas.legalservices.opinionview.model.OpinionView;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpinionViewMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private CaseListEntry caseListEntry;
	private OpinionView opinionView;
	public Optional<CaseListEntry>  getCaseListEntry() {
		return Optional.ofNullable(caseListEntry);
	}
	public Optional<OpinionView>  getOpinionView() {
		return Optional.ofNullable(opinionView);
	}
}
