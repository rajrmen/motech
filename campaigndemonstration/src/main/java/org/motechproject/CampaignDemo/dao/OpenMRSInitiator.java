package org.motechproject.CampaignDemo.dao;
import java.util.List;

import org.motechproject.openmrs.Context;
import org.openmrs.EncounterType;
import org.openmrs.api.EncounterService;
import org.springframework.beans.factory.annotation.Autowired;

public class OpenMRSInitiator {
	
	public void bootstrap() {
		System.out.println("Attempting to do open MRS stuff");
	}
}
