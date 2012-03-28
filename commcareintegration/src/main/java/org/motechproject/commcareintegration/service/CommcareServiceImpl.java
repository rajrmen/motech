package org.motechproject.commcareintegration.service;

import java.io.IOException;
import java.util.List;

import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.HttpClient;
import org.motechproject.commcareintegration.domain.CaseInstance;
import org.motechproject.dao.MotechJsonReader;
import org.springframework.beans.factory.annotation.Autowired;

public class CommcareServiceImpl implements CommcareService {


	MotechJsonReader motechReader;
	HttpClient commonsHttpClient;

	@Autowired
	public CommcareServiceImpl(HttpClient commonsHttpClient) {
		this(commonsHttpClient, new MotechJsonReader());
	}

	public CommcareServiceImpl(HttpClient commonsHttpClient, MotechJsonReader motechReader) {
		this.commonsHttpClient = commonsHttpClient;
		this.motechReader = motechReader;
	}

	@Override
	public List<CaseInstance> getCasesByUserId(String userId, String domain) throws HttpException, IOException {
		// TODO Auto-generated method stub
		String urlPath = "https://www.commcarehq.org/a/" + domain + "/cloudcare/api/cases/?user_id=" + userId;
		System.out.println(urlPath);

		HttpMethod getMethod = new GetMethod(urlPath);
		commonsHttpClient.getParams().setAuthenticationPreemptive(true);
		commonsHttpClient.getState().setCredentials(new AuthScope(null, 80, "DJANGO", "digest"), new UsernamePasswordCredentials("kebooo@hotmail.com", "keboobek00")); 
		int status = commonsHttpClient.executeMethod(getMethod);
		String response = getMethod.getResponseBodyAsString();
		Header[] headers = getMethod.getResponseHeaders();
		HttpMethod getMethod2 = new GetMethod(urlPath);
		for (Header header : headers) {
			if (header.getName().equals("Set-Cookie")) {
				if (header.getValue().contains("sessionid")) {
					String[] split = header.getValue().split(";");
					getMethod2.addRequestHeader("Cookie", split[0]);
				}
				
			}
		}
		headers = getMethod2.getRequestHeaders();
		for (Header header : headers) {
			System.out.println(header.getName() + " " + header.getValue());
		}
		status = commonsHttpClient.executeMethod(getMethod2);
		response = getMethod2.getResponseBodyAsString();
		System.out.println(response);
		return null;
	}

	@Override
	public List<CaseInstance> getCaseByUserId(String userId, String caseId, String domain) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CaseInstance> getCasesByUserIdAndCaseType(String userId,
			String caseType, String domain) {
		// Commcare case type from Commcare HQ API currently not working
		return null;
	}

}
