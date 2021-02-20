package statutes.service.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import statutes.StatutesBaseClass;

@JsonPropertyOrder({ "statutesPath", "finalReferences" })
public class StatuteHierarchySave {

	@JsonInclude
    protected List<StatutesBaseClass> statutesPath;
	@JsonInclude
    protected List<StatutesBaseClass> finalReferences;

    public List<StatutesBaseClass> getStatutesPath() {
        if (statutesPath == null) {
        	statutesPath = new ArrayList<>();
        }
        return this.statutesPath;
    }

    public List<StatutesBaseClass> getFinalReferences() {
        if (finalReferences == null) {
        	finalReferences = new ArrayList<>();
        }
        return this.finalReferences;
    }
}
