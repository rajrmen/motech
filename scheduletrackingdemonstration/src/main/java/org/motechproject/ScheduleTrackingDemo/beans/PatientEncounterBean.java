package org.motechproject.ScheduleTrackingDemo.beans;

import java.util.Date;

import org.motechproject.mobileforms.api.domain.FormBean;

public class PatientEncounterBean extends FormBean {

	private static final long serialVersionUID = 1L;
	private String motechId;
	private Date observedDate;
	private int observedConcept;
	
	public String getMotechId() {
		return motechId;
	}
	
	public void setMotechId(String motechId) {
		this.motechId = motechId;
	}
	
	public Date getObservedDate() {
		return observedDate;
	}
	
	public void setObservedDate(Date observedDate) {
		this.observedDate = observedDate;
	}
	
	public int getObservedConcept() {
		return observedConcept;
	}
	
	public void setObservedConcept(int observedConcept) {
		this.observedConcept = observedConcept;
	}
}
