package opjpa;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

//import opca.mailer.EmailInformation;
import opca.service.OpinionViewSingleton;
import opca.service.ViewParameters;
import opca.view.CaseView;
import opca.view.OpinionView;
import opca.view.SectionView;

@SpringBootApplication(scanBasePackages = { "opca", "opjpa" })
@ConditionalOnProperty(name = "TestSlipOpinion.active", havingValue = "true", matchIfMissing = false)
public class TestSlipOpinion implements ApplicationRunner {

	Logger logger = Logger.getLogger(TestSlipOpinion.class.getName());

	public static void main(String[] args) throws Exception {
		SpringApplication.run(TestSlipOpinion.class, args);
	}

	@Autowired
	private OpinionViewSingleton slipOpinionSingleton;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		System.out.println(slipOpinionSingleton.checkStatus());

//	        Calendar calNow = Calendar.getInstance();
//	        Calendar calLastWeek = Calendar.getInstance();
//	        int year = calLastWeek.get(Calendar.YEAR);
//	        int dayOfYear = calLastWeek.get(Calendar.DAY_OF_YEAR);
//	        dayOfYear = dayOfYear - 7;
//	        if ( dayOfYear < 1 ) {
//	            year = year - 1;
//	            dayOfYear = 365 + dayOfYear;
//	        }
//	        calLastWeek.set(Calendar.YEAR, year);
//	        calLastWeek.set(Calendar.DAY_OF_YEAR, dayOfYear);

		LocalDate startDate = LocalDate.of(2020, 10, 15);
		LocalDate endDate = LocalDate.of(2020, 10, 22);

		List<OpinionView> opinionCases = slipOpinionSingleton
				.getOpinionCases(new ViewParameters(startDate, endDate));
//		System.out.println(opinionCases);
		opinionCases.forEach(this::opinionView);
		
//<div class="ophead">
//  <span class="ophead date"><h:outputText value="#{cc.attrs.view.opinionDate}" ><f:convertDateTime pattern="MMM dd"/></h:outputText>
//  </span>
//  <ui:fragment rendered="#{!opinionsController.opinionOpen(cc.attrs.view.name)}">
//    <h:commandLink actionListener="#{opinionsController.openOpinion(cc.attrs.view.name)}"><span class="ophead title"><h:outputText value="#{cc.attrs.view.title}" /></span></h:commandLink>
//  </ui:fragment>
//  <ui:fragment rendered="#{opinionsController.opinionOpen(cc.attrs.view.name)}">
//    <h:commandLink actionListener="#{opinionsController.closeOpinion()}"><span class="ophead title"><h:outputText value="#{cc.attrs.view.title}" /></span></h:commandLink>
//  </ui:fragment>
//  <h:outputLink styleClass="ophead right" value="http://www.courts.ca.gov/opinions/documents/#{cc.attrs.view.fileName}.PDF"><h:outputText value="#{cc.attrs.view.fileName}"/></h:outputLink>
//</div>
//<ui:fragment rendered="#{opinionsController.opinionOpen(cc.attrs.view.name)}">
//  <div class="openstat wrap">
//    <ui:repeat var="section" value="#{cc.attrs.view.sectionViews}" >
//      <div class="openstat srow"><span class="casestar">
//      <ui:repeat var="i" value="#{opinionsController.repeatNTimes(section.importance)}">
//          <span class="glyphicon glyphicon-star"/>      
//      </ui:repeat>
//      </span>
//      <span class="openstat code titlepath"><h:outputText value="#{section.displayTitlePath}" /></span>
//<!--           
//          <span class="openstat code sections"><h:outputText value="#{section.displaySections}" /></span>
// -->          
//          <h:outputLink value="http://op-opca.b9ad.pro-us-east-1.openshiftapps.com/?path=#{section.fullFacet}" ><span class="openstat code sections"><h:outputText value="#{section.displaySections}" /></span></h:outputLink>
//      </div>
//    </ui:repeat>
//  </div>
//  <div class="opencase wrap"><div class="opencase casehead"><span class="opencase case">Cases Cited:</span></div>
//    <div class="opencase casewrap">
//    <ui:repeat var="opinion" value="#{cc.attrs.view.cases}" size="#{cc.attrs.view.cases.size()>10?10:cc.attrs.view.cases.size()}" >
//      <div class="opencase orow"><span class="casestar">
//      <ui:repeat var="i" value="#{opinionsController.repeatNTimes(opinion.importance)}">
//          <span class="glyphicon glyphicon-star"/>      
//      </ui:repeat>
//      </span>
//      <span class="opencase title"><h:outputText value="#{opinion.title}"/></span>
//      <span class="opencase citedetails"><ui:fragment rendered="#{not empty opinion.opinionDate}"> (<h:outputText value="#{opinion.opinionDate}"><f:convertDateTime pattern="yyyy"/> </h:outputText>) </ui:fragment><h:outputText value="#{opinion.citation}"/></span>
//      </div>          
//    </ui:repeat>
//    </div>
//    <ui:fragment rendered="#{cc.attrs.view.cases.size() > 10}"><div class="opencase casehead"><span class="opencase case">[<h:outputText value="#{cc.attrs.view.cases.size() - 10}"/> more cases cited.]</span></div></ui:fragment>
//  </div>
// </ui:fragment>
// <div class="summary wrap"><div class="summary outer"><div class="summary inner"><span>
// <b>
// <ui:fragment rendered="#{cc.attrs.view.disposition != null}" ><h:outputText value="#{cc.attrs.view.disposition}"/></ui:fragment>
// <ui:fragment rendered="#{cc.attrs.view.disposition == null}"><h:outputText value="Disposition Unknown"/></ui:fragment>
// </b>
// </span>
// <ui:fragment rendered="#{cc.attrs.view.summary != null}" >&nbsp;<h:outputText value="#{cc.attrs.view.summary}"/></ui:fragment>
// <ui:fragment rendered="#{cc.attrs.view.summary == null and cc.attrs.view.publicationStatus != null}"><h:outputText value="#{cc.attrs.view.publicationStatus}"/></ui:fragment>
// </div>
// </div></div>
//<div class="opinion tail" />

	}
	
	private void opinionView(OpinionView opinionView) {
		System.out.println(opinionView.getTitle() + " : " + opinionView.getOpinionDate() + " : " + opinionView.getFileName());
		System.out.println("-------------------------------");
		for (SectionView sectionView: opinionView.getSectionViews() ) {
			System.out.println("\t" + sectionView.getDisplayTitlePath() + " : " + sectionView.getDisplaySections() ) ;	
		}
		System.out.println("-------------------------------");
		for ( CaseView caseView : opinionView.getCases()) {
			System.out.println("\t" + caseView.getTitle() + " : " + caseView.getOpinionDate() + " : " + caseView.getCitation() ) ;	
		}
		System.out.println(opinionView.getDisposition());
		System.out.println(opinionView.getSummary());
		System.out.println("===============================");
	}
}