package com.github.karlnicholas.legalservices.slipopinion.parser;

import java.util.List;

import com.github.karlnicholas.legalservices.caselist.model.CaseListEntry;
import com.github.karlnicholas.legalservices.opinion.parser.ScrapedOpinionDocument;
import com.github.karlnicholas.legalservices.slipopinion.model.SlipOpinion;

public interface OpinionScraperInterface {
	List<CaseListEntry> getCaseList();
	List<ScrapedOpinionDocument> scrapeOpinionFiles(List<CaseListEntry> slipOpinions);
	ScrapedOpinionDocument scrapeOpinionFile(SlipOpinion slipOpinion);
}
