package model;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

//{
//	  "resource_uri": "https://www.courtlistener.com:80/api/rest/v3/clusters/3303287/",
//	  "id": 3303287,
//	  "absolute_url": "/opinion/3303287/dyer-v-mccorkle/",
//	  "panel": [],
//	  "non_participating_judges": [],
//	  "docket": "https://www.courtlistener.com:80/api/rest/v3/dockets/3175408/",
//	  "sub_opinions": [
//	    "https://www.courtlistener.com:80/api/rest/v3/opinions/3302604/",
//	    "https://www.courtlistener.com:80/api/rest/v3/opinions/3302605/"
//	  ],
//	  "citations": [
//	    {
//	      "volume": 208,
//	      "reporter": "Cal.",
//	      "page": "216",
//	      "type": 2
//	    },
//	    {
//	      "volume": 280,
//	      "reporter": "P.",
//	      "page": "965",
//	      "type": 3
//	    }
//	  ],
//	  "judges": "PRESTON, J.",
//	  "date_created": "2016-07-05T17:18:32.816935Z",
//	  "date_modified": "2017-04-14T23:29:44.628617Z",
//	  "date_filed": "1929-09-26",
//	  "date_filed_is_approximate": false,
//	  "slug": "dyer-v-mccorkle",
//	  "case_name_short": "Dyer",
//	  "case_name": "Dyer v. McCorkle",
//	  "case_name_full": "Dayton Dyer, an Infant, Etc. v. B.T. McCorkle",
//	  "federal_cite_one": "",
//	  "federal_cite_two": "",
//	  "federal_cite_three": "",
//	  "state_cite_one": "208 Cal. 216",
//	  "state_cite_two": "",
//	  "state_cite_three": "",
//	  "state_cite_regional": "280 P. 965",
//	  "specialty_cite_one": "",
//	  "scotus_early_cite": "",
//	  "lexis_cite": "",
//	  "westlaw_cite": "",
//	  "neutral_cite": "",
//	  "scdb_id": "",
//	  "scdb_decision_direction": null,
//	  "scdb_votes_majority": null,
//	  "scdb_votes_minority": null,
//	  "source": "Z",
//	  "procedural_history": "",
//	  "attorneys": "Fenton, Williams  Hansen and Frank Kauke for Appellants.\n\nGallaher  Jertberg for Respondent.",
//	  "nature_of_suit": "",
//	  "posture": "APPEAL from a judgment of the Superior Court of Fresno County. Denver S. Church, Judge. Affirmed.\n\nThe facts are stated in the opinion of the court.",
//	  "syllabus": "",
//	  "citation_count": 9,
//	  "precedential_status": "Published",
//	  "date_blocked": null,
//	  "blocked": false
//	}
public class CourtListenerCluster implements ParsedOpinion {
	@Data
	public static class Citation {
		public int volume;
		public String reporter;
		public String page;
		public int type;
	}

	public String resource_uri;
	public long id;
	public String absolute_url;
	public List<Object> panel;
	public List<Object> non_participating_judges;
	public String docket;
	public List<String> sub_opinions;
	@JsonIgnore
	public List<CourtListenerOpinion> opinions;
	public List<Citation> citations;
	public String judges;
	public Date date_created;
	public Date date_modified;
	public String date_filed;
	public boolean date_filed_is_approximate;
	public String slug;
	public String case_name_short;
	public String case_name;
	public String case_name_full;
	public String federal_cite_one;
	public String federal_cite_two;
	public String federal_cite_three;
	public String state_cite_one;
	public String state_cite_two;
	public String state_cite_three;
	public String state_cite_regional;
	public String specialty_cite_one;
	public String scotus_early_cite;
	public String lexis_cite;
	public String westlaw_cite;
	public String neutral_cite;
	public String scdb_id;
	public Object scdb_decision_direction;
	public Object scdb_votes_majority;
	public Object scdb_votes_minority;
	public String source;
	public String procedural_history;
	public String attorneys;
	public String nature_of_suit;
	public String posture;
	public String syllabus;
	public String headnotes;
	public String summary;
	public String disposition;
	public String history;
	public String other_dates;
	public String cross_reference;
	public String correction;
	public int citation_count;
	public String precedential_status;
	public Object date_blocked;
	public boolean blocked;
	public String filepath_json_harvard;
	@Override
	public String getCaseName() {
		return case_name;
	}
	@Override
	public LocalDate getDateFiled() {
		return LocalDate.parse(date_filed);
	}

}
