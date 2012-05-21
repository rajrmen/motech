package org.motechproject.commcare.util;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CommCareAPIHttpClient {
	
	   private HttpClient commonsHttpClient;
	   
	   private Properties commcareUserProperties;
	   
	   @Autowired
	   public CommCareAPIHttpClient(HttpClient commonsHttpClient, @Qualifier(value = "commcareUserApi")Properties commcareUserProperties) {
			this.commonsHttpClient = commonsHttpClient;
			this.commcareUserProperties = commcareUserProperties;
	   }
	   
	   public String usersRequest() {
		   return this.getRequest(commcareUserUrl(), null);
	   }
	   
	   public String casesRequest(NameValuePair[] queryParams) {
		   return this.getRequest(baseCommcareUrl(), queryParams);
	   }
	   
		private HttpMethod buildRequest(String url, NameValuePair[] queryParams) {
			HttpMethod requestMethod = new GetMethod(url);

			commonsHttpClient.getParams().setAuthenticationPreemptive(true);

			commonsHttpClient.getState()
			.setCredentials(new AuthScope(null, -1, null, null),
					new UsernamePasswordCredentials(getUsername(), 
							getPassword()));

			if (queryParams != null) {
				requestMethod.setQueryString(queryParams);
			}

			return requestMethod;
		}
	   
	   private String getRequest(String requestUrl, NameValuePair[] queryParams) {
		   
			HttpMethod getMethod = buildRequest(requestUrl, queryParams);
			
			String response = "";
			
			try {
				int status = commonsHttpClient.executeMethod(getMethod);
				response = getMethod.getResponseBodyAsString();
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return response;
	   }
	   
		private String commcareUserUrl() {
			return "https://www.commcarehq.org/a/" + 
					getCommcareDomain() + 
					"/api/v0.1/user/?format=json";
		}
		
		private String baseCommcareUrl() {
			return "https://www.commcarehq.org/a/" + 
					getCommcareDomain() + 
					"/cloudcare/api/cases/";
		}
	   
		private String getCommcareDomain() {
			return commcareUserProperties.getProperty("commcareDomain");
		}
		
		private String getUsername() {
			return commcareUserProperties.getProperty("username");
		}
		
		private String getPassword() {
			return commcareUserProperties.getProperty("password");
		}
}
