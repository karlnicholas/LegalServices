package guidedsearchweb.controller;

import java.io.IOException;
//import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import statutes.service.StatutesService;
import statutes.service.client.StatutesServiceClientImpl;
import gsearch.GSearch;
import gsearch.viewmodel.ViewModel;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/")
@SpringBootApplication
public class GuidedSearchController {
//	private static final Logger logger = Logger.getLogger(GuidedSearchController.class.getName());		
	private UrlBuilder urlBuilder = new UrlBuilder();

	public static void main(String[] args) {
		SpringApplication.run(GuidedSearchController.class, args);
	}

	@GetMapping("/")
	protected Mono<ViewModel> doGet(@RequestParam(required = false) String path, @RequestParam(required = false) String term, @RequestParam(required = false) boolean frag) throws IOException {

		StatutesService statutesService = new StatutesServiceClientImpl("http://localhost:8090/");
		GSearch gsearch = new GSearch(statutesService);
		
		Mono<ViewModel> viewModel = gsearch.handleRequest(path, term, frag);
//		request.setAttribute("viewModel", viewModel );

////		viewModel.setTerm(StringEscapeUtils.escapeHtml4( viewModel.getTerm() ));
//		setAdvancedSearchFields(request, term);
	
		// process the requests with in the MODEL
//		logger.fine("1: State = " + viewModel.getState());
//		logger.fine("1: Path = " + viewModel.getPath() + ": Term = " + viewModel.getTerm());

//		request.getRequestDispatcher("/WEB-INF/views/search.jsp").forward(request, response);
		return viewModel;
	}
/*
	@PostMapping("/")
	protected void doPost() throws ServletException, IOException {
		String path = request.getParameter("path"); 
		String term = request.getParameter("term"); 
		boolean frag = Boolean.parseBoolean( request.getParameter("frag") ); 
		String ntm = request.getParameter("ntm"); 
		String cl = request.getParameter("cl"); 
		String to = request.getParameter("to");
		String inAll = request.getParameter("inAll"); 
		String inNot = request.getParameter("inNot");
		String inAny = request.getParameter("inAny");
		String inExact = request.getParameter("inExact"); 
    	
    	// navbar toggle fragments
		if ( to != null ) frag = !frag;
		
		// navbar copy of the (possibly new) term;
//		if ( !ntm.isEmpty() ) term = ntm;
//		term = ntm;

		// navbar clear term and fragments
		String bTerm = null;
		if ( 
			cl == null && (
			!inAll.isEmpty()
			|| !inNot.isEmpty()
			|| !inAny.isEmpty()
			|| !inExact.isEmpty()
			)
		) {
			StringBuilder sb = new StringBuilder();
			if ( !inAll.isEmpty() ) {
				sb.append(appendOp(inAll, '+'));
			}
			if ( !inNot.isEmpty() ) {
				sb.append(appendOp(inNot,'-'));
			}
			if ( !inAny.isEmpty() ) {
				sb.append(inAny + " ");
			}
			if ( !inExact.isEmpty() ) {
				sb.append("\"" + inExact + "\"");
			}
			bTerm = sb.toString().trim();
		}
		if ( cl != null ) {
			term = null;
			frag = false;
		} else if ( bTerm != null && ( term == null || !bTerm.equals(term) && ntm.equals(term)) ) {
			term = bTerm;
		} else if ( !ntm.equals(term) ) {
			term = ntm;
		}
				
		response.sendRedirect("/"+urlBuilder.UrlArgs(path, term, frag));    	
    }
*/    
    private String appendOp(String val, char op) {
    	val = val.trim();
    	if ( val.isEmpty()) return "";
    	String[] terms = val.trim().split(" ");
    	StringBuilder sb = new StringBuilder();
    	for ( String term: terms ) {
    		sb.append(op+term+" ");
    	}
    	return sb.toString();
    }
  
/*
    private void setAdvancedSearchFields(HttpServletRequest request, String term) {
    	if ( term == null || term.isEmpty() ) return;
    	try {
	    	String[] terms = term.split(" ");
	    	String all = new String();
	    	String not = new String();
	    	String any = new String();
	    	String exact = new String();
	    	boolean ex = false;
	    	for(String t: terms) {
	    		if ( !ex && t.startsWith("+")) all=all.concat(t.substring(1) + " ");
	    		else if ( !ex && t.startsWith("-")) not=not.concat(t.substring(1) + " " );
	    		else if ( !ex && (t.startsWith("\"") && t.trim().endsWith("\"")) ) {
	    			exact=exact.concat(t.substring(1, t.length()-1) + " ");
	    		}
	    		else if ( !ex && t.startsWith("\"")) {
	    			exact=exact.concat(t.substring(1) + " ");
	    			ex = true;
	    		}
	    		else if ( ex && !t.endsWith("\"") ) {
	    			exact=exact.concat(t) + " ";
	    		}
	    		else if ( ex && t.endsWith("\"")) {
	    			exact=exact.concat(t.substring(0, t.length()-1)) + " ";
	    			ex = false;
	    		}
	    		else any = any.concat(t) + " ";
	    	}
	    	if ( !all.isEmpty() ) request.setAttribute("inAll", all.trim());
	    	if ( !not.isEmpty() ) request.setAttribute("inNot", not.trim());
	    	if ( !any.isEmpty() ) request.setAttribute("inAny", any.trim());
	    	if ( !exact.isEmpty() ) request.setAttribute("inExact", exact.trim());
    	} catch ( Throwable t) {
    		// silent exception
    		logger.warning("Exception:" + t.getMessage());
    	}
    }
*/
}

