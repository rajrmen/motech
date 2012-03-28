package org.motechproject.commcareintegration.service;

import java.io.IOException;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.motechproject.commcareintegration.domain.CaseInstance;

public interface CommcareService {
	
	public List<CaseInstance> getCasesByUserId(String userId, String domain) throws HttpException, IOException;
	public List<CaseInstance> getCaseByUserId(String userId, String caseId, String domain);
	public List<CaseInstance> getCasesByUserIdAndCaseType(String userId, String caseType, String domain);

}
