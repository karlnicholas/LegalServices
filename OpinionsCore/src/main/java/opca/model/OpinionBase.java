package opca.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.*;

import opca.memorydb.CitationStore;
import opca.parser.ParsedOpinionCitationSet;

@SuppressWarnings("serial")
@Entity
@NamedQueries({
	@NamedQuery(name="OpinionBase.findOpinionByKeyFetchReferringOpinions", 
		query="select distinct o from OpinionBase o left join fetch o.referringOpinions where o.opinionKey = :key"),
	@NamedQuery(name="OpinionBase.opinionsWithReferringOpinions", 
		query="select distinct o from OpinionBase o left join fetch o.referringOpinions where o.opinionKey in :opinionKeys"),
//	@NamedQuery(name="OpinionBase.fetchOpinionCitationsForOpinions", 
//	query="select distinct o from OpinionBase o left join fetch o.opinionCitations ooc left join fetch ooc.statuteCitations oocsc left join fetch oocsc.statuteCitation where o.id in :opinionIds"), 
	@NamedQuery(name="OpinionBase.fetchOpinionCitationsForOpinions", 
		query="select distinct o from OpinionBase o where o.id in :opinionIds"), 
	@NamedQuery(name="OpinionBase.fetchCitedOpinionsWithReferringOpinions", 
		query="select distinct oro from OpinionBase o2 left outer join o2.opinionCitations oro left join fetch oro.referringOpinions where o2.id in :opinionIds"),
	
	})
@NamedEntityGraphs({ 
	@NamedEntityGraph(name="fetchGraphForSlipOpinions", attributeNodes= {
		@NamedAttributeNode(value="opinionCitations", subgraph="fetchGraphForSlipOpinionsPartB")
	}, 
	subgraphs= {
		@NamedSubgraph(
			name = "fetchGraphForSlipOpinionsPartB", 
			attributeNodes = { @NamedAttributeNode(value = "statuteCitations", subgraph="fetchGraphForSlipOpinionsPartC") } 
		),
		@NamedSubgraph(
			name = "fetchGraphForSlipOpinionsPartC", 
			attributeNodes = { @NamedAttributeNode(value = "statuteCitation") } 
		),
	}) 
})
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType=DiscriminatorType.INTEGER)
@Table(indexes= {@Index(columnList = "vset,volume,page")})
public class OpinionBase implements Comparable<OpinionBase>, Serializable {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	protected Integer id;
	protected OpinionKey opinionKey;
	@Column(columnDefinition="varchar(127)")
	protected String title;
    @Temporal(TemporalType.DATE)
    protected Date opinionDate;
	@OneToMany(mappedBy="opinionBase")
	protected Set<OpinionStatuteCitation> statuteCitations;
    @ManyToMany
    protected Set<OpinionBase> opinionCitations;
    @ManyToMany(mappedBy="opinionCitations")
    protected Set<OpinionBase> referringOpinions;
    // performance optimization equal to size of referringOpinions 
    protected int countReferringOpinions;
	@Transient
	private boolean newlyLoadedOpinion;

    public OpinionBase() {}
	public OpinionBase(OpinionBase opinionBase) {
		this.opinionKey = opinionBase.opinionKey;
    	setTitle(opinionBase.title);
    	this.opinionDate = opinionBase.opinionDate;
    	this.statuteCitations = opinionBase.statuteCitations;
    	this.opinionCitations = opinionBase.opinionCitations;
    	this.referringOpinions = opinionBase.referringOpinions;
    	this.countReferringOpinions = opinionBase.countReferringOpinions;
    }
	public OpinionBase(OpinionKey opinionKey, String title, Date opinionDate, String court) {
		this.opinionKey = opinionKey;
		setTitle(title);
    	this.opinionDate = opinionDate;
    	this.newlyLoadedOpinion = true;
    }
	// making a new OpinionBase from only a citation.
	public OpinionBase(int volume, int vset, int page) {
		this(new OpinionKey(volume, vset, page), null, null, null);
	}
	// making a new OpinionBase from only a citation.
    public OpinionBase(OpinionBase opinionBase, String volume, String vset, String page) {
    	this(new OpinionKey(volume, vset, page));
    	addReferringOpinion(opinionBase);
    	this.newlyLoadedOpinion = false;
    }
	
	/**
	 * Only meant for comparison purposes.
	 * @param opinionKey for opinion
	 */
    public OpinionBase(OpinionKey opinionKey) {
        this.opinionKey = opinionKey;
    }
	/**
	 * adds a new referringOpinion key if it doesn't already exist.
	 * @param opinionBase OpinionBase 
	 */
    public void addReferringOpinion(OpinionBase opinionBase) {
    	if (referringOpinions == null ) {
    		setReferringOpinions(new TreeSet<OpinionBase>());
    	}
    	referringOpinions.add(opinionBase);
        // do it the paranoid way
        countReferringOpinions = referringOpinions.size();
    }
    /**
     * Removes a referringOpinion if it exists
     * @param opinionBase OpinionBase
     */
    public void removeReferringOpinion(OpinionBase opinionBase) {
    	if (referringOpinions != null ) {
	    	if ( referringOpinions.remove(opinionBase) ) {
	    		countReferringOpinions = referringOpinions.size();
	    	}
    	}
    }
	public void addStatuteCitations(Set<StatuteCitation> goodStatutes) {
		if ( statuteCitations == null ) {
			setStatuteCitations(new TreeSet<>());
		}
		for( StatuteCitation statuteCitation: goodStatutes) {
			// add on both sides ...
			OpinionStatuteCitation opinionStatuteCitation = statuteCitation.getOpinionStatuteReference(this);
			if ( opinionStatuteCitation == null ) {
				throw new RuntimeException("OpinionStatuteReference not found");
			}
			// complete the other side of the reference.
			statuteCitations.add(opinionStatuteCitation);
		}
	}
	
	public Collection<StatuteCitation> getOnlyStatuteCitations() {
		if ( statuteCitations == null ) {
			setStatuteCitations(new TreeSet<>());
		}
		Set<StatuteCitation> onlyStatuteCitations = new TreeSet<>();
		for(OpinionStatuteCitation opinionStatuteCitation: statuteCitations) {
			onlyStatuteCitations.add(opinionStatuteCitation.getStatuteCitation());
		}
		return onlyStatuteCitations;
	}	
    public Integer getId() {
		return id;
	}
    public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		if ( title != null && title.length() > 127 ) title = title.substring(0, 127);
		this.title = title;
	}
	public Date getOpinionDate() {
		return opinionDate;
	}
	public void setOpinionDate(Date opinionDate) {
		this.opinionDate = opinionDate;
	}
	public Set<OpinionStatuteCitation> getStatuteCitations() {
		return statuteCitations;
	}
	public void setStatuteCitations(Set<OpinionStatuteCitation> statuteCitations) {
		this.statuteCitations = statuteCitations;
	}
	public Set<OpinionBase> getOpinionCitations() {
		return opinionCitations;
	}
	public void setOpinionCitations(Set<OpinionBase> opinionCitations) {
		this.opinionCitations = opinionCitations;
	}
	public Set<OpinionBase> getReferringOpinions() {
        return referringOpinions;
    }
    public void setReferringOpinions(Set<OpinionBase> referringOpinions) {
        this.referringOpinions = referringOpinions;
        countReferringOpinions = referringOpinions.size();
    }
    public int getCountReferringOpinions() {
    	return countReferringOpinions;
    }
	public void checkCountReferringOpinions() {
		countReferringOpinions = referringOpinions.size();
	}
	public void setCountReferringOpinions(int countReferringOpinions) {
		this.countReferringOpinions = countReferringOpinions;
	}
	public OpinionKey getOpinionKey() {
		return opinionKey;
	}
	public void setOpinionKey(OpinionKey opinionKey) {
		this.opinionKey = opinionKey ;
	}
	@Override
	public int hashCode() {
		return opinionKey.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
//		if (getClass() != obj.getClass())
//			return false;
		if ( !(obj instanceof OpinionBase) ) 
			return false;
		OpinionBase other = (OpinionBase) obj;
		return opinionKey.equals(other.opinionKey);
		// id may not be initialized from database during initial load historical opinions.
//		return id.equals(other.id);
	}
	@Override
	public int compareTo(OpinionBase o) {
		return opinionKey.compareTo(o.opinionKey);
	}
	public void mergeCitedOpinion(OpinionBase opinionBase) {
		// debug
        if ( !opinionKey.equals(opinionBase.getOpinionKey())) throw new RuntimeException("Can not add modifications: " + opinionKey + " != " + opinionBase.getOpinionKey());
//        if ( opinionBase.statuteCitations != null ) throw new RuntimeException("Can not add modifications: opinionBase.statuteCitations != null");
//        if ( opinionBase.opinionCitations != null ) throw new RuntimeException("Can not add modifications: opinionBase.opinionCitations != null");
//        if ( opinionBase.referringOpinions.size() != 1 ) throw new RuntimeException("Can not add modifications: " + opinionBase.referringOpinions.size() + " != 1");
        //
//        mergeOpinions(opinionBase);
        // this is a newly cited opinion, so there will be only one referring opinion
    	if (referringOpinions == null ) {
    		setReferringOpinions(new TreeSet<OpinionBase>());
    	}
    	referringOpinions.addAll(opinionBase.referringOpinions);
        // do it the paranoid way
        countReferringOpinions = referringOpinions.size();
    }
	public void mergePersistenceFromSlipLoad(OpinionBase opinionBase) {
		// debug
        if ( !opinionKey.equals(opinionBase.getOpinionKey())) throw new RuntimeException("Can not add modifications: " + opinionKey + " != " + opinionBase.getOpinionKey());
        if ( opinionBase.isNewlyLoadedOpinion() ) throw new RuntimeException("Can not add modifications: " + opinionKey + " != " + opinionBase.getOpinionKey());
        // 
        // opinionBase is either a published opinion (slip opinions) or a cited opinion 
        // at this point, it's coming from slipOpinions, so if it's a slipOpinion
        // this existingOpinion will NOT be a slip opinion .. since we don't merge slip opinions.
        // if the existing opinion is not a slip opinion, and opinionbase is not a slip opinion, then is 
        // is a case of merging cited opinions.
        if ( opinionBase.getStatuteCitations() != null ) throw new RuntimeException("Can not add modifications: opinionBase.statuteCitations != null");
        if ( opinionBase.getOpinionCitations() != null ) throw new RuntimeException("Can not add modifications: opinionBase.opinionCitations != null");
        // replace ALL the opinionBase in the referring slipOpinion
        Iterator<OpinionBase> roIt = opinionBase.getReferringOpinions().iterator();
        while ( roIt.hasNext() ) {
        	OpinionBase ro = roIt.next();
        	ro.opinionCitations.remove(opinionBase);
        	ro.opinionCitations.add(this);
        }
        //
    	if (referringOpinions == null ) {
    		setReferringOpinions(new TreeSet<OpinionBase>());
    	}
    	//TODO:Lazy initialization exception ... 
    	// these are newly created entities, so how did they get 'detached'?
    	referringOpinions.addAll(opinionBase.getReferringOpinions());
        // do it the paranoid way
        countReferringOpinions = referringOpinions.size();
	}
	public void mergePublishedOpinion(OpinionBase opinionBase) {
		// debug
        if ( !opinionKey.equals(opinionBase.getOpinionKey())) throw new RuntimeException("Can not add modifications: " + opinionKey + " != " + opinionBase.getOpinionKey());
        if ( opinionBase.referringOpinions != null ) throw new RuntimeException("Can not add modifications: " + opinionKey + " != " + opinionBase.getOpinionKey());
        //
        if ( title == null ) title = opinionBase.title;
        if ( opinionDate == null ) opinionDate = opinionBase.opinionDate;
        
        copyNewOpinions(opinionBase);
        // do statutes .. 
        if ( opinionBase.getStatuteCitations() != null ) {
        	if ( statuteCitations == null ) 
        		statuteCitations = new TreeSet<OpinionStatuteCitation>();
	        for ( OpinionStatuteCitation addStatuteCitation: opinionBase.getStatuteCitations() ) {
            	if ( addStatuteCitation.getStatuteCitation().getStatuteKey().getLawCode() == null ) 
            		continue;
	            if ( !statuteCitations.contains(addStatuteCitation) ) {
	            	statuteCitations.add(addStatuteCitation);
	            }
	        }
        }
        // do 
		if ( newlyLoadedOpinion || opinionBase.isNewlyLoadedOpinion() )
			newlyLoadedOpinion = true;
        // do referringOpinions
        // this is a "published" opinion, so there will be no referring opinions in opinionBase
	}

	private void copyNewOpinions(OpinionBase opinionBase) {
        // do opinions
        if ( opinionBase.getOpinionCitations() != null ) {
        	if ( opinionCitations == null )
        		opinionCitations = new TreeSet<OpinionBase>(); 
	        for ( OpinionBase addOpinionBase: opinionBase.getOpinionCitations()) {
	        	if ( !opinionCitations.contains(addOpinionBase) ) {
	        		opinionCitations.add(addOpinionBase);
	        	}
	        }
        }
	}
	public void mergeCourtRepublishedOpinion(OpinionBase opinionBase, ParsedOpinionCitationSet parserResults, CitationStore citationStore ) {
        if ( title == null ) title = opinionBase.title;
        if ( opinionDate == null ) opinionDate = opinionBase.opinionDate;
		copyNewOpinions(opinionBase);
        // do statutes .. 
        if ( opinionBase.getStatuteCitations() != null ) {
        	if ( statuteCitations == null ) 
        		statuteCitations = new TreeSet<OpinionStatuteCitation>();
	        for ( OpinionStatuteCitation addStatuteCitation: opinionBase.getStatuteCitations() ) {
            	if ( addStatuteCitation.getStatuteCitation().getStatuteKey().getLawCode() == null ) 
            		continue;
	            if ( !statuteCitations.contains(addStatuteCitation) ) {
	            	statuteCitations.add(addStatuteCitation);
	            } else {
	            	StatuteCitation newCitation = parserResults.findStatute(addStatuteCitation.getStatuteCitation().getStatuteKey());
	            	StatuteCitation existingCitation = citationStore.statuteExists(addStatuteCitation.getStatuteCitation());
	            	// because the opinionBase is actually an existing one so maybe not a citation reference in it.
	            	if ( newCitation != null ) {
		            	OpinionStatuteCitation osR = newCitation.getOpinionStatuteReference(opinionBase);
		            	int countNew= osR == null ? 0 : osR.getCountReferences();
		            	osR = existingCitation.getOpinionStatuteReference(opinionBase);
		            	int countExisting = osR == null ? 0 : osR.getCountReferences();
		            	if ( countExisting < countNew ) {
		            		existingCitation.setRefCount(opinionBase, newCitation.getOpinionStatuteReference(opinionBase).getCountReferences());
		            	}
	            	}
	            }
	        }
        }
	}
	public boolean isNewlyLoadedOpinion() {
		return newlyLoadedOpinion;
	}
	public void setNewlyLoadedOpinion(boolean newlyLoadedOpinion) {
		this.newlyLoadedOpinion = newlyLoadedOpinion;
	}
	@Override
	public String toString() {
        return String.format("%1$s : %2$tm/%2$td/%2$ty : %3$S", getOpinionKey().toString(), getOpinionDate(), getTitle() );
    }
	public String fullPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%1$S : %2$tm/%2$td/%2$ty : %3$S", getOpinionKey().toString(), getOpinionDate(), getTitle() ));
		sb.append('\n');
		sb.append("statuteCitations");
		sb.append('\n');
		for ( OpinionStatuteCitation statuteCitation: statuteCitations ) {
			sb.append(statuteCitation);
			sb.append('\n');
		};
		sb.append("opinionCitations");
		sb.append('\n');
		for ( OpinionBase opinionCitation: opinionCitations ) {
			sb.append(opinionCitation);
			sb.append('\n');
		};
		sb.append("referringOpinions");
		sb.append('\n');
		if( referringOpinions != null ) {
			for ( OpinionBase referringOpinion: referringOpinions ) {
				sb.append(referringOpinion);
				sb.append('\n');
			};
		}
		sb.append('\n');
		sb.append("countReferringOpinions");
		sb.append('\n');
		sb.append(countReferringOpinions);
		sb.append('\n');
		return sb.toString();
    }
}
