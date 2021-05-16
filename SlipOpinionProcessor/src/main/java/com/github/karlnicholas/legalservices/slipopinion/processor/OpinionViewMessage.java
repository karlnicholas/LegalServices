package com.github.karlnicholas.legalservices.slipopinion.processor;

import java.util.Optional;

import com.github.karlnicholas.legalservices.caselist.model.CaseListEntry;
import com.github.karlnicholas.legalservices.opinionview.model.OpinionView;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpinionViewMessage {
	Optional<CaseListEntry> caseListEntry;
	Optional<OpinionView> opinionView;
}
