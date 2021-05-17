package com.github.karlnicholas.legalservices.slipopinion.parser;

import java.io.IOException;
import java.util.List;

import com.github.karlnicholas.legalservices.caselist.model.CaseListEntries;
import com.github.karlnicholas.legalservices.opinion.parser.ScrapedOpinionDocument;
import com.github.karlnicholas.legalservices.slipopinion.model.SlipOpinion;

public interface OpinionScraperInterface {
	CaseListEntries getCaseList() throws IOException;
	List<ScrapedOpinionDocument> scrapeOpinionFiles(CaseListEntries slipOpinions);
	ScrapedOpinionDocument scrapeOpinionFile(SlipOpinion slipOpinion) throws IOException;
}
