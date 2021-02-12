package opjpa;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashSet;

import opca.model.OpinionBase;
import opca.model.OpinionKey;
import opca.model.OpinionStatuteCitation;
import opca.model.SlipOpinion;
import opca.model.StatuteCitation;

public class DecodeClass {
	SlipOpinion slipOpinion;
	String id;
	public DecodeClass(Tuple tuple) {
		slipOpinion = new SlipOpinion(
				tuple.get("filename").toString(), 
				tuple.get("fileextension").toString(), 
				String.valueOf(tuple.get("o_title")), 
				Date.from(LocalDate.parse(tuple.get("o_opiniondate").toString()).atStartOfDay().toInstant(ZoneOffset.UTC)), 
				"court", 
				"searchUrl"
			);
		slipOpinion.setStatuteCitations(new HashSet<>());
		slipOpinion.setOpinionCitations(new HashSet<>());
		decode(tuple);
		id = tuple.get("o_id").toString();
	}
	
	public SlipOpinion getSlipOpinion() {
		return slipOpinion;
	}
	public String getId() {
		return id;
	};
	private DecodeClass decode(Tuple tuple) {
		// do StatuteCitation
		if ( tuple.get("sc_lawcode") != null && tuple.get("sc_sectionnumber") != null ) {
			StatuteCitation statuteCitation = new StatuteCitation(slipOpinion, 
					tuple.get("sc_lawcode").toString(), 
					tuple.get("sc_sectionnumber").toString());
			statuteCitation.setDesignated(Boolean.parseBoolean(tuple.get("sc_designated").toString()));
			OpinionStatuteCitation opinionStatuteCitation = new OpinionStatuteCitation(statuteCitation, slipOpinion, 
					Integer.parseInt(tuple.get("osc_countreferences").toString()));
			if ( !slipOpinion.getStatuteCitations().contains(opinionStatuteCitation)) {
				slipOpinion.getStatuteCitations().add(opinionStatuteCitation); 
			}
		}
		if ( tuple.get("oc_page") != null && tuple.get("oc_volume") != null && tuple.get("oc_vset") != null ) {
			// do OpinionCitation
			OpinionKey opinionKey = new OpinionKey(
					Integer.parseInt(tuple.get("oc_page").toString()), 
					Integer.parseInt(tuple.get("oc_volume").toString()), 
					Integer.parseInt(tuple.get("oc_vset").toString())
				);
			OpinionBase opinionCitation = new OpinionBase(opinionKey);
			OpinionBase finalOpinionCitation = slipOpinion.getOpinionCitations().stream().filter(oc->oc.equals(opinionCitation)).findAny().orElseGet(
				()->{
					opinionCitation.setCountReferringOpinions(Integer.parseInt(tuple.get("oc_countreferringopinions").toString()));
					if ( tuple.get("oc_opiniondate") != null ) {
						opinionCitation.setOpinionDate(Date.from(LocalDate.parse(tuple.get("oc_opiniondate").toString()).atStartOfDay().toInstant(ZoneOffset.UTC)));
					}
					opinionCitation.setStatuteCitations(new HashSet<>());
					slipOpinion.getOpinionCitations().add(opinionCitation); 
					return opinionCitation;}
			);
			// do OpinionCitation->StatuteCitation
			if ( tuple.get("ocsc_lawcode") != null && tuple.get("ocsc_sectionnumber") != null ) {
				StatuteCitation statuteCitation = new StatuteCitation(slipOpinion, 
						tuple.get("ocsc_lawcode").toString(), 
						tuple.get("ocsc_sectionnumber").toString());
				statuteCitation.setDesignated(Boolean.parseBoolean(tuple.get("ocsc_designated").toString()));
				OpinionStatuteCitation opinionStatuteCitation = new OpinionStatuteCitation(statuteCitation, slipOpinion, 
						Integer.parseInt(tuple.get("ocosc_countreferences").toString()));		

				if ( !finalOpinionCitation.getStatuteCitations().contains(opinionStatuteCitation)) {
					finalOpinionCitation.getStatuteCitations().add(opinionStatuteCitation); 
				}
			}
		}
		
		return this;
	}
	public DecodeClass combine(DecodeClass decodeClass) {
		// do StatuteCitation
		slipOpinion.mergePublishedOpinion(decodeClass.slipOpinion);
		return this;
	}
}

//select 
//o.id as o_id,
//o.countreferringopinions as o_countreferringopinions,
//o.opiniondate as o_opiniondate,
//o.page as o_page,
//o.volume as o_volume,
//o.vset as o_vset,
//o.title as o_title,
//osc.countreferences as osc_countreferences,
//sc.designated as sc_designated,
//sc.lawcode as sc_lawcode,
//sc.sectionnumber as sc_sectionnumber,
//oc.countreferringopinions as oc_countreferringopinions,
//oc.opiniondate as oc_opiniondate,
//oc.page as oc_page,
//oc.volume as oc_volume,
//oc.vset as oc_vset,
//oc.title as oc_title,
//ocosc.countreferences as ocosc_countreferences,
//ocsc.id as ocsc_id,
//ocsc.designated as ocsc_designated,
//ocsc.lawcode as ocsc_lawcode,
//ocsc.sectionnumber as ocsc_sectionnumber,
//sp.filename,
//sp.fileextension
//from opinionbase o 
//left outer join opinionstatutecitation osc on osc.opinionbase_id = o.id 
//left outer join statutecitation sc on sc.id = osc.statutecitation_id
//left outer join opinionbase_opinioncitations oboc on oboc.referringopinions_id = o.id
//left outer join opinionbase oc on oc.id = oboc.opinioncitations_id
//left outer join opinionstatutecitation ocosc on  ocosc.opinionbase_id = oc.id 
//left outer join statutecitation ocsc on ocsc.id = ocosc.statutecitation_id
//join slipproperties sp on o.id = sp.slipopinion_id
//where o.dtype=-1344462334;
