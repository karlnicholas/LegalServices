package com.gitub.karlnicholas.legalservices.statute.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.github.karlnicholas.legalservices.statute.StatuteKey;
import com.github.karlnicholas.legalservices.statute.StatutesBaseClass;


@JsonPropertyOrder({ "statuteKey", "statutesBaseClass" })
public class FindStatutesResponse {

	@JsonInclude
    protected StatuteKey statuteKey;
	@JsonInclude
    protected StatutesBaseClass statutesBaseClass;

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

    /**
     * Gets the value of the statutesBaseClass property.
     * 
     * @return
     *     possible object is
     *     {@link StatutesBaseClass }
     *     
     */
    public StatutesBaseClass getStatutesBaseClass() {
        return statutesBaseClass;
    }

    /**
     * Sets the value of the statutesBaseClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatutesBaseClass }
     *     
     */
    public void setStatutesBaseClass(StatutesBaseClass value) {
        this.statutesBaseClass = value;
    }

}
