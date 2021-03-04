package opca.service;

import org.springframework.stereotype.Component;

@Component
public class OpinionViewSingleton {
//	private final OpinionViewLoad opinionViewLoad;
//	private final OpinionViewData opinionViewData;
//	public OpinionViewSingleton(OpinionViewLoad opinionViewLoad, OpinionViewData opinionViewData) {
//		this.opinionViewLoad = opinionViewLoad;
//		this.opinionViewData = opinionViewData;
//	}
//	public boolean checkStatus() {
//		boolean ready = false;
//		if ( !opinionViewData.isReady() ) {
//			if ( !opinionViewData.isLoaded() ) {
//				opinionViewLoad.load(opinionViewData, StatutesServiceFactory.getStatutesServiceClient());
//			}		
//		} else {
//			ready = true;
//		}
//		return ready;
//	}
//	/*
//	 * Dynamic method (for now?)
//	 */
//	public List<OpinionView> getOpinionCases(ViewParameters viewInfo) {
//		List<OpinionView> opinionViewList = copyCasesForViewinfo(viewInfo);
//		viewInfo.totalCaseCount = opinionViewList.size();
//		viewInfo.accountCaseCount = opinionViewList.size();
//		return opinionViewList;
//	}
//	
//	/*
//	 * Dynamic method 
//	 */
//	public List<OpinionView> getOpinionCasesForAccount(ViewParameters viewInfo, User user) {
//		List<OpinionView> opinionViewList = copyCasesForViewinfo(viewInfo);
//		viewInfo.totalCaseCount = opinionViewList.size();
//		viewInfo.accountCaseCount = opinionViewList.size();
//	    Set<String> userTitles = new HashSet<>();
//
//    	for ( String c: user.getTitles()) {
//    		userTitles.add(c);	
//        }
//
//    	Iterator<OpinionView> ovIt = opinionViewList.iterator();
//    	while ( ovIt.hasNext() ) {
//			OpinionView opinionView = ovIt.next();
//			boolean foundOne = false;
//			for ( SectionView sectionView: opinionView.getSectionViews() ) {
//				ViewReference parent = sectionView.getParent();
//				while ( parent.getParent() != null ) {
//					parent = parent.getParent();
//				}
//				for ( String title: userTitles) {
//					if ( parent.getShortTitle().equals( title ) ) {
//						foundOne = true;
//						break;
//					}
//				}
//				if ( foundOne ) {
//					break;
//				}
//			}
//			if ( !foundOne ) {
//				ovIt.remove();
//			}
//    	}
//		return opinionViewList;
//	}
//
//	private List<OpinionView> copyCasesForViewinfo(ViewParameters viewInfo) {
//		List<OpinionView> opinionViewCopy = new ArrayList<OpinionView>();
//		for (OpinionView opinionCase: opinionViewData.getOpinionViews() ) {
//			if ( 
//				opinionCase.getOpinionDate().compareTo(viewInfo.sd) >= 0  
//				&& opinionCase.getOpinionDate().compareTo(viewInfo.ed) <= 0
//			) {
//				opinionViewCopy.add(opinionCase);
//			}
//		}
//		return opinionViewCopy;
//	}
//	
//	public LocalDate dateParam(String startDate) {
//    	LocalDate dateParam = null;
//    	if ( startDate != null && !startDate.trim().isEmpty() ) {
//				dateParam = LocalDate.parse(startDate);
//    	}
//    	return dateParam;
//	}
//	
//	public int currentDateIndex(String startDate) {
//		LocalDate dateParam = dateParam(startDate);
//    	int i=0;
//    	int currentIndex = 0;
//    	LocalDate dateRecent = opinionViewData.getReportDates().get(0)[0];    	
//    	for ( LocalDate[] dates: opinionViewData.getReportDates() ) {
//    		if ( dates[0] == null || dates[1] == null ) continue;  
//    		if ( dateParam != null ) {
//	    		if ( dateParam.compareTo(dateRecent) < 0 ) {
//	    			dateRecent = dates[0];
//	    	    	currentIndex = i;
//	    		} 
//    		}
//    		i++;
//    	}
//    	return currentIndex;
//	}
//
//	public boolean isReady() {
//		return opinionViewData.isReady();
//	}
//
//	public List<LocalDate[]> getReportDates() {
//		return opinionViewData.getReportDates();
//	}
//
//	public void updateOpinionViews(List<OpinionKey> opinionKeys, StatutesService statutesService) {
//		if ( opinionViewData.isReady() ) {
//			opinionViewLoad.loadNewOpinions(opinionViewData, opinionKeys, statutesService);
//		}
//	}
//	public List<String[]> getStringDateList() {
//		return opinionViewData.getStringDateList();
//	}
}