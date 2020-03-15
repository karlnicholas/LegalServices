package statutes.service.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import statutes.StatutesBaseClass;

@JsonPropertyOrder({ "statuteKey", "statutesPath" })
public class KeyHierarchyPair {

	@JsonInclude
    protected StatuteKey statuteKey;
	@JsonInclude
    protected List<StatutesBaseClass> statutesPath;

    /**
     * Gets the value of the statuteKey property.
     * 
     * @return
     *     possible object is
     *     {@link StatuteKey }
     *     
     */
    public StatuteKey getStatuteKey() {
        return statuteKey;
    }

    /**
     * Sets the value of the statuteKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatuteKey }
     *     
     */
    public void setStatuteKey(StatuteKey value) {
        this.statuteKey = value;
    }

	public List<StatutesBaseClass> getStatutesPath() {
		return statutesPath;
	}

	public void setStatutesPath(List<StatutesBaseClass> statutesPath) {
		this.statutesPath = statutesPath;
	}

}
