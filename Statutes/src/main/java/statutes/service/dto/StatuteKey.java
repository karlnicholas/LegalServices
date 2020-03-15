package statutes.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "lawCode", "sectionNumber" })
public class StatuteKey {

	@JsonInclude
    protected String lawCode;
	@JsonInclude
    protected String sectionNumber;

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLawCode() {
        return lawCode;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLawCode(String value) {
        this.lawCode = value;
    }

    /**
     * Gets the value of the sectionNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSectionNumber() {
        return sectionNumber;
    }

    /**
     * Sets the value of the sectionNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSectionNumber(String value) {
        this.sectionNumber = value;
    }

}
