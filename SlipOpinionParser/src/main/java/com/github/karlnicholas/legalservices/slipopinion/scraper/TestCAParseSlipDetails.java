package com.github.karlnicholas.legalservices.slipopinion.scraper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.github.karlnicholas.legalservices.caselist.model.CaseListEntries;
import com.github.karlnicholas.legalservices.caselist.model.CaseListEntry;
import com.github.karlnicholas.legalservices.opinion.parser.ScrapedOpinionDocument;

import com.github.karlnicholas.legalservices.slipopinion.model.SlipOpinion;

public class TestCAParseSlipDetails extends CACaseScraper {
	private static final Logger logger = Logger.getLogger(TestCAParseSlipDetails.class.getName());

	public TestCAParseSlipDetails(boolean debugFiles) {
		super(debugFiles);
	}
	
	@Override
	public CaseListEntries getCaseList() {
		try {
			return parseCaseList(new FileInputStream( CACaseScraper.caseListDir + "/" +  CACaseScraper.caseListFile ));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<ScrapedOpinionDocument> scrapeOpinionFiles(CaseListEntries caseListEntries) {
		List<ScrapedOpinionDocument> documents = new ArrayList<ScrapedOpinionDocument>();		
		CAParseScrapedDocument parseScrapedDocument = new CAParseScrapedDocument();
		for (CaseListEntry caseListEntry: caseListEntries) {
			SlipOpinion slipOpinion = new SlipOpinion(caseListEntry.getFileName(), caseListEntry.getFileExtension(), caseListEntry.getTitle(), caseListEntry.getOpinionDate(), caseListEntry.getCourt(), caseListEntry.getSearchUrl());
			try ( InputStream inputStream = Files.newInputStream( Paths.get(casesDir + slipOpinion.getFileName() + slipOpinion.getFileExtension())) ) {
				ScrapedOpinionDocument parsedDoc = parseScrapedDocument.parseScrapedDocument(slipOpinion, inputStream);
	        	if ( parsedDoc.isScrapedSuccess() ) {
	        		documents.add( parsedDoc );
//					parseOpinionDetails(slipOpinion);
				} else {
					logger.warning("Opinion not parsed: " + slipOpinion.getFileName() + " " + slipOpinion.getFileExtension());
				}
				inputStream.close();
			} catch (IOException e) {
				logger.severe("Parse Scraped Document: " + e.getMessage());
			}

			boolean goodtogo = true;
			try ( InputStream inputStream = Files.newInputStream( 
					Paths.get(casesDir + slipPropertyFilename(slipOpinion.getFileName(), mainCaseScreen))) 
			) {
				goodtogo = parseMainCaseScreenDetail(inputStream, slipOpinion); 
			} catch (IOException e) {
				logger.warning("File error: " + e.getMessage());
				goodtogo = false;
			}
			if ( !goodtogo ) {
				continue;
			}
			
			try ( InputStream inputStream = Files.newInputStream( 
					Paths.get(casesDir + slipPropertyFilename(slipOpinion.getFileName(), disposition))) 
			) {
				parseDispositionDetail(inputStream, slipOpinion); 
			} catch (IOException e) {
				logger.warning("File error: " + e.getMessage());
			}
			try ( InputStream inputStream = Files.newInputStream( 
					Paths.get(casesDir + slipPropertyFilename(slipOpinion.getFileName(), partiesAndAttorneys))) 
			) {
				parsePartiesAndAttorneysDetail(inputStream, slipOpinion); 
			} catch (IOException e) {
				logger.warning("File error: " + e.getMessage());
			}
			try ( InputStream inputStream = Files.newInputStream( 
					Paths.get(casesDir + slipPropertyFilename(slipOpinion.getFileName(), trialCourt))) 
			) {
				parseTrialCourtDetail(inputStream, slipOpinion); 
			} catch (IOException e) {
				logger.warning("File error: " + e.getMessage());
			}
/*			
			String caption = slipOpinion.getSummary().getCaseCaption();
			if ( caption != null )
				slipOpinion.setTitle(caption);
			String division = slipOpinion.getSummary().getDivision();
			if ( division != null )
				slipOpinion.setCourt(division );
			Date dispositionDate = slipOpinion.getDisposition().getDate();
			if ( dispositionDate != null )
				slipOpinion.setOpinionDate(dispositionDate);
*/					
		}		
		return documents;
		
	}

	@Override
	public ScrapedOpinionDocument scrapeOpinionFile(SlipOpinion slipOpinion) {
		CAParseScrapedDocument parseScrapedDocument = new CAParseScrapedDocument();
		ScrapedOpinionDocument parsedDoc = null;
		try ( InputStream inputStream = Files.newInputStream( Paths.get(casesDir + slipOpinion.getFileName() + slipOpinion.getFileExtension())) ) {
			parsedDoc = parseScrapedDocument.parseScrapedDocument(slipOpinion, inputStream);
        	if ( parsedDoc.isScrapedSuccess() ) {
//					parseOpinionDetails(slipOpinion);
			} else {
				logger.warning("Opinion not parsed: " + slipOpinion.getFileName() + " " + slipOpinion.getFileExtension());
			}
			inputStream.close();
		} catch (IOException e) {
			logger.severe("Parse Scraped Document: " + e.getMessage());
		}
		boolean goodtogo = true;
		try ( InputStream inputStream = Files.newInputStream( 
				Paths.get(casesDir + slipPropertyFilename(slipOpinion.getFileName(), mainCaseScreen))) 
		) {
			goodtogo = parseMainCaseScreenDetail(inputStream, slipOpinion); 
		} catch (IOException e) {
			logger.warning("File error: " + e.getMessage());
			goodtogo = false;
		}
		if ( goodtogo ) {
			try ( InputStream inputStream = Files.newInputStream( 
					Paths.get(casesDir + slipPropertyFilename(slipOpinion.getFileName(), disposition))) 
			) {
				parseDispositionDetail(inputStream, slipOpinion); 
			} catch (IOException e) {
				logger.warning("File error: " + e.getMessage());
			}
			try ( InputStream inputStream = Files.newInputStream( 
					Paths.get(casesDir + slipPropertyFilename(slipOpinion.getFileName(), partiesAndAttorneys))) 
			) {
				parsePartiesAndAttorneysDetail(inputStream, slipOpinion); 
			} catch (IOException e) {
				logger.warning("File error: " + e.getMessage());
			}
			try ( InputStream inputStream = Files.newInputStream( 
					Paths.get(casesDir + slipPropertyFilename(slipOpinion.getFileName(), trialCourt))) 
			) {
				parseTrialCourtDetail(inputStream, slipOpinion); 
			} catch (IOException e) {
				logger.warning("File error: " + e.getMessage());
			}
		}
		return parsedDoc;
	}

}
