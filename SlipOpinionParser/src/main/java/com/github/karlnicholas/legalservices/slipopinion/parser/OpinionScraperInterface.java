package com.github.karlnicholas.legalservices.slipopinion.parser;

import java.util.List;

import com.github.karlnicholas.legalservices.opinion.parser.ScrapedOpinionDocument;
import com.github.karlnicholas.legalservices.slipopinion.model.SlipOpinion;

public interface OpinionScraperInterface {
	List<SlipOpinion> getCaseList();
	List<ScrapedOpinionDocument> scrapeOpinionFiles(List<SlipOpinion> slipOpinions);
	ScrapedOpinionDocument scrapeOpinionFile(SlipOpinion slipOpinion);
}
