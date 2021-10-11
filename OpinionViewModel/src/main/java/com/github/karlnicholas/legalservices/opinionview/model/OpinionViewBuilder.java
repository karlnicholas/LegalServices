package com.github.karlnicholas.legalservices.opinionview.model;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.github.karlnicholas.legalservices.statute.SectionNumber;
import com.github.karlnicholas.legalservices.statute.StatuteKey;
import com.github.karlnicholas.legalservices.statute.StatutesBaseClass;
import com.github.karlnicholas.legalservices.statute.StatutesLeaf;
import com.github.karlnicholas.legalservices.statute.StatutesNode;
import com.github.karlnicholas.legalservices.statute.StatutesRoot;
import com.github.karlnicholas.legalservices.statute.service.StatuteService;
import com.github.karlnicholas.legalservices.opinion.model.*;
import com.github.karlnicholas.legalservices.slipopinion.model.SlipOpinion;

public class OpinionViewBuilder {
    private static final int LEVEL_OF_INTEREST = 3;
    private final StatuteService statutesService;
//	private List<SectionView> sectionViews;

    public OpinionViewBuilder(StatuteService statutesService) {
        this.statutesService = statutesService;
    }

    public OpinionView buildOpinionView(
            SlipOpinion slipOpinion
    ) {
        List<StatuteView> statuteViews = createStatuteViews(slipOpinion);
//            this.parserResults = parserResults;
        // create a CaseView list.
        List<CaseView> cases = new ArrayList<CaseView>();
        for (OpinionBase opinionBase : slipOpinion.getOpinionCitations()) {
            CaseView caseView = new CaseView(opinionBase.getTitle(), opinionBase.getOpinionKey().toString(), opinionBase.getOpinionDate(), opinionBase.getCountReferringOpinions());
            cases.add(caseView);
        }
        OpinionView opinionView = new OpinionView(slipOpinion, slipOpinion.getFileName(), statuteViews, cases);
        scoreCitations(opinionView, slipOpinion.getOpinionCitations(), cases, opinionView.getSectionViews(), statuteViews);
        return opinionView;
    }

    public List<StatuteView> createStatuteViews(OpinionBase opinionBase) {
        List<StatuteKey> statuteKeys = opinionBase.getOnlyStatuteCitations().stream()
                .map(statuteCitation -> {
                    StatuteKey statuteKey = new StatuteKey();
                    statuteKey.setLawCode(statuteCitation.getStatuteKey().getLawCode());
                    statuteKey.setSectionNumber(statuteCitation.getStatuteKey().getSectionNumber());
                    return statuteKey;
                }).collect(Collectors.toList());
        // call statutesws to get details of statutes
        // Flux<KeyHierarchyPair> keyHierarchyPairs =
        // statutesRs.getStatutesAndHierarchies(statuteKeyFlux);
        //
        List<StatutesRoot> statuteHierarchies = statutesService.getStatutesAndHierarchies(statuteKeys).getBody().getStatuteRoots();
        // set parents within hierarchy.
        statuteHierarchies.forEach(statutesRoot -> statutesRoot.rebuildParentReferences(statutesRoot));
        List<StatuteView> statuteViews = new ArrayList<>();
        // copy results into the new list ..
        Iterator<OpinionStatuteCitation> itc = opinionBase.getStatuteCitations().iterator();

        while (itc.hasNext()) {
            OpinionStatuteCitation citation = itc.next();
            if (citation.getStatuteCitation().getStatuteKey().getLawCode() != null) {
                // find the statutesLeaf from the Statutes service and return with all parents
                // filled out.
                findStatutesLeaf(statuteHierarchies, citation.getStatuteCitation().getStatuteKey())
                        .ifPresent((statutesLeaf) -> {
                            // get the root
                            StatutesBaseClass statutesRoot = statutesLeaf;
                            while (statutesRoot.getParent() != null) {
                                statutesRoot = statutesRoot.getParent();
                            }
                            // see if StatuteView has already been created
                            StatuteView statuteView = findExistingStatuteView(statuteViews, statutesRoot);
                            if (statuteView == null) {
                                // else, construct one ...
                                statuteView = new StatuteView();
                                statuteView.initialize((StatutesRoot) statutesRoot, 0, null);
                                statuteViews.add(statuteView);
                            }
                            addSectionViewToSectionRoot(statuteView, statutesLeaf, citation.getCountReferences());
                        });

            }
        }
        trimToLevelOfInterest(statuteViews, LEVEL_OF_INTEREST, true);
        return statuteViews;
    }

    /**
     * Take statute hierarchy in statutesLeaf and merge into
     * ViewReference hierarchy in statuteView. Add reference counts
     * into existing ViewReference hierarchy.
     *
     * @param statuteView  existing StatuteView root
     * @param statutesLeaf new StatutesLeaf citation
     * @param refCount     reference count of slip opinion for this statutesLeaf
     */
    private void addSectionViewToSectionRoot(
            StatuteView statuteView,
            StatutesLeaf statutesLeaf,
            int refCount
    ) {
        Stack<StatutesNode> statutesNodes = new Stack<>();
        StatutesBaseClass parent = statutesLeaf;
        // push nodes onto stack
        while ((parent = parent.getParent()) != null && parent instanceof StatutesNode) {
            statutesNodes.push((StatutesNode) parent);
        }
        // prime childrenViews
        statuteView.incRefCount(refCount);
        List<ViewReference> childrenViews = statuteView.getChildReferences();
        // setup view parent
        ViewReference viewParent = statuteView;
        // start stack loop.
        while (!statutesNodes.isEmpty()) {
            StatutesNode statutesNode = statutesNodes.pop();
            // do childrenViews check
            viewParent = checkChildrenViews(childrenViews, statutesNode, refCount, viewParent, SubcodeView::new);
            childrenViews = viewParent.getChildReferences();
        }
        checkChildrenViews(childrenViews, statutesLeaf, refCount, viewParent, SectionView::new);
    }

    /**
     * Check to see if the statutesBaseClass is in the existing childrenViews
     * by checking title though might change it to fullFacet.
     * Create new viewReference if needed with statutesBaseClass and refCount.
     * Return  the childrenViews of new or existing childView.
     *
     * @param childrenViews            list of ViewReferences to check against.
     * @param statutesBaseClass        of statutesNode or StatutesLeaf
     * @param refCount                 of statutesBaseClass
     * @param viewReferenceConstructor function to create new ViewReference if needed
     * @return childrenViews of next level in hierarchy.
     */
    private ViewReference checkChildrenViews(
            List<ViewReference> childrenViews,
            StatutesBaseClass statutesBaseClass,
            int refCount,
            ViewReference parent,
            Supplier<ViewReference> viewReferenceConstructor
    ) {
        boolean found = false;
        ViewReference childView = null;
        Iterator<ViewReference> vrIt = childrenViews.iterator();
        while (vrIt.hasNext()) {
            childView = vrIt.next();
            if (childView.getTitle().equals(statutesBaseClass.getTitle())) {
                found = true;
                break;
            }
        }
        if (found) {
            childView.incRefCount(refCount);
            return childView;
        } else {
            ViewReference viewReference = viewReferenceConstructor.get();
            viewReference.initialize(statutesBaseClass, refCount, parent);
            childrenViews.add(viewReference);
            return viewReference;
        }
    }

    /**
     * Trim the ViewReference hierarchy where and refCount less than levelOfInterest.
     * If removeStatuteViews then remove any statuteViews that don't meet requirement.
     * <p>
     * If there isn't a sectionView at the end of the chain then need to remove entire tree(branch).
     *
     * @param levelOfInterest    level to trim to.
     * @param removeStatuteViews remove statuteViews
     */
    private void trimToLevelOfInterest(List<StatuteView> statuteViews, int levelOfInterest, boolean removeStatuteViews) {
        Iterator<StatuteView> ci = statuteViews.iterator();
        while (ci.hasNext()) {
            StatuteView statuteView = ci.next();
            statuteView.trimToLevelOfInterest(levelOfInterest);
            if (removeStatuteViews) {
                if (statuteView.getRefCount() < levelOfInterest)
                    ci.remove();
            }
        }
    }

    /**
     * Build out the StatutesBaseClass with parent pointers
     *
     * @param statutesRoots
     * @param key
     * @return
     */
    private Optional<StatutesLeaf> findStatutesLeaf(List<StatutesRoot> statutesRoots, StatuteKey key) {
        return statutesRoots.stream()
                .filter(statutesRoot -> statutesRoot.getLawCode().equalsIgnoreCase(key.getLawCode()))
                .map(statutesRoot -> {
                    SectionNumber sectionNumber = new SectionNumber();
                    sectionNumber.setPosition(-1);
                    sectionNumber.setSectionNumber(key.getSectionNumber());
                    return Optional.ofNullable(statutesRoot.findReference(sectionNumber));
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(StatutesLeaf.class::cast)
                .findFirst();
/*
    	List<StatutesBaseClass> subPaths = null;
    	final String lawCode = key.getLawCode();
    	final String sectionNumber = key.getSectionNumber();
    	
    	for ( StatutesBaseClass statutesBaseClass: statuteHierarchy.getFinalReferences()) {
    		if ( lawCode.equals(statutesBaseClass.getLawCode()) && sectionNumber.equals(statutesBaseClass.getStatuteRange().getsNumber().getSectionNumber()) ) {
    			subPaths = statutesBaseClass.getReferences();
    			break;
    		}
    	}
		StatutesBaseClass returnBaseClass = null;
    	if ( subPaths != null ) {
			StatutesRoot statutesRoot = (StatutesRoot)subPaths.get(0);
			StatutesBaseClass parent = statutesRoot;
			returnBaseClass = statutesRoot;
			for (StatutesBaseClass baseClass: subPaths ) {
				// check terminating
				baseClass.setParent(parent);
				parent = baseClass; 
				returnBaseClass = baseClass;
				subPaths = baseClass.getReferences();
			}
    	}
    	return (StatutesLeaf)returnBaseClass;
*/
    }


    private StatuteView findExistingStatuteView(List<StatuteView> statutesViews, StatutesBaseClass statutesRoot) {
        Iterator<StatuteView> cit = statutesViews.iterator();
        while (cit.hasNext()) {
            StatuteView statuteView = cit.next();
            if (statuteView.getShortTitle().equals(statutesRoot.getShortTitle()))
                return statuteView;
        }
        return null;
    }

//	public ParsedOpinionCitationSet getParserResults() {
//		return parserResults;
//	}    

//	private ParsedOpinionCitationSet parserResults;
//	private OpinionView opinionView;
//	private List<CaseView> cases; 
//	private List<SectionView> sectionViews;

    // end: supporting methods for JSF pages
//	OpinionView opinionView, 
//	ParsedOpinionCitationSet parserResults, 
//	List<CaseView> cases

    private OpinionView scoreCitations(
            OpinionView opinionView,
            Set<OpinionBase> opinionsCited,
            List<CaseView> cases,
            List<SectionView> sectionViews,
            List<StatuteView> statuteViews
    ) {
        scoreSlipOpinionStatutes(sectionViews);
        // create a union of all statutes from the slipOpinion and the cited cases
        List<SectionView> sectionUnion = new ArrayList<>(opinionView.getSectionViews());
        List<OpinionView> tempOpinionViewList = new ArrayList<>();
        // need a collection StatutueCitations.

        for (OpinionBase opinionCited : opinionsCited) {
            List<StatuteView> localStatuteViews = createStatuteViews(opinionCited);
            List<SectionView> sectionViewsLocal = new ArrayList<>();
            OpinionView tempOpinionView = new OpinionView();
            for (StatuteView statuteView : localStatuteViews) {
                sectionViewsLocal.addAll(statuteView.getSectionViews());
            }
            rankSectionViews(sectionViewsLocal);
            // create a temporary OpinionView to use its functions
            // store the opinionView in the Cases list.
            List<CaseView> tempCaseViews = new ArrayList<>();
            CaseView caseView = findCaseView(opinionCited, cases);
            tempCaseViews.add(caseView);
            tempOpinionView.setStatutes(localStatuteViews);
            tempOpinionView.setCases(tempCaseViews);
            if (tempOpinionView.getStatutes().size() == 0) {
                caseView.setScore(-1);
                // well, just remove cases with no interesting citations
                cases.remove(caseView);
                continue;
            }
            // save this for next loop
            tempOpinionViewList.add(tempOpinionView);
            for (SectionView sectionView : sectionViews) {
                if (!sectionUnion.contains(sectionView)) {
                    sectionUnion.add(sectionView);
                }
            }
        }

        // create a ranked slipAdjacencyMatrix
        int[] slipAdjacencyMatrix = createAdjacencyMatrix(sectionUnion, sectionViews);
        for (OpinionView tempOpinionView : tempOpinionViewList) {

            int[] opinionAdjacencyMatrix = createAdjacencyMatrix(sectionUnion, tempOpinionView.getSectionViews());
            // find the distance between the matrices
            int sum = 0;
            for (int i = slipAdjacencyMatrix.length - 1; i >= 0; --i) {
                sum = sum + Math.abs(slipAdjacencyMatrix[i] - opinionAdjacencyMatrix[i]);
            }
            tempOpinionView.getCases().get(0).setScore(sum);
        }
        // rank "scores"
        // note that scores is actually a distance computation
        // and lower is better
        // so the final result is subtracted from 4
        // because importance is higher is better.
        long maxScore = 0;
        for (CaseView c : cases) {
            if (c.getScore() > maxScore)
                maxScore = c.getScore();
        }
        double d = ((maxScore + 1) / 4.0);
        for (CaseView c : cases) {
            if (c.getScore() == -1) {
                c.setImportance(0);
            } else {
                int imp = (int) (((double) c.getScore()) / d) + 1;
                c.setImportance(5 - imp);
            }
        }
        Collections.sort(cases, new Comparator<CaseView>() {
            @Override
            public int compare(CaseView o1, CaseView o2) {
                return o2.getImportance() - o1.getImportance();
            }
        });
        return opinionView;
    }

    /**
     * This is only a single row adjacency matrix because all of the rows
     * past the first row would contain 0's and they would not be used in
     * the distance computation
     *
     * @param sectionUnion
     * @param sectionViews
     * @return
     */
    private int[] createAdjacencyMatrix(
            List<SectionView> sectionUnion,
            List<SectionView> sectionViews
    ) {
        int[] adjacencyMatrix = new int[sectionUnion.size()];
        int i = 0;
        for (SectionView unionSectionView : sectionUnion) {
            int idx = sectionViews.indexOf(unionSectionView);
            adjacencyMatrix[i++] = idx == -1 ? 0 : sectionViews.get(idx).getImportance();
        }
        return adjacencyMatrix;
    }

    private void scoreSlipOpinionStatutes(List<SectionView> sectionViews) {
        rankSectionViews(sectionViews);
/*		
		Collections.sort(sectionViews, new Comparator<SectionView>() {
			@Override
			public int compare(SectionView o1, SectionView o2) {
				return o2.getImportance() - o1.getImportance();
			}
		});
*/
    }

    /**
     * Rank from 0-4
     *
     * @param sectionViews
     */
    private void rankSectionViews(List<SectionView> sectionViews) {
        long maxCount = 0;
        for (SectionView c : sectionViews) {
            if (c.getRefCount() > maxCount)
                maxCount = c.getRefCount();
        }
        // Don't scale any refCounts. If less than 4 then leave as is.
        if (maxCount < 4) maxCount = 4;
        double d = ((maxCount + 1) / 5.0);
        for (SectionView c : sectionViews) {
            c.setImportance((int) (((double) c.getRefCount()) / d));
        }
    }

    public CaseView findCaseView(OpinionBase opinionCited, List<CaseView> cases) {
        for (CaseView caseView : cases) {
            if (caseView.getCitation().equals(opinionCited.getOpinionKey().toString())) {
                return caseView;
            }
        }
        return null;
    }
}
