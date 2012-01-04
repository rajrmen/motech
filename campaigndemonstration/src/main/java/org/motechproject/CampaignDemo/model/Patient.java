package org.motechproject.CampaignDemo.model;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;


@TypeDiscriminator("doc.type === 'PATIENT'")
public class Patient extends MotechAuditableDataObject {
	
	@JsonProperty("type") 
	private final String type = "PATIENT";
    
	@JsonProperty
	private String externalid;
    @JsonProperty
	private String phoneNum;
//	private String format = "a";
//	private String language = "b";
	
    
    public Patient() {
    	
    }
    
    public Patient(String externalid) {
    	this.externalid = externalid;
    }
    
	public Patient(String externalid, String phoneNum) {
		this.externalid = externalid;
		this.phoneNum = phoneNum;
	}
	
	public String getPhoneNum() {
		return phoneNum;
	}
	
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	
	public String getExternalid() {
		return externalid;
	}
	
	public void setExternalId(String externalId) {
		this.externalid = externalId;
	}
	
	public boolean equals(Object o) {
		Patient patient = (Patient) o;
		if (patient.getExternalid().equals(externalid)) return true;
		return false;
	}
	
//	public String getFormat() {
//		return format;
//	}
	
//	private String getLanguage() {
//		return language;
//	}
//	
//	private void setFormat(String format) {
//		this.format = format;
//	}
//	
//	private void setLanguage(String language) {
//		this.language = language;
//	}
	
	
}
