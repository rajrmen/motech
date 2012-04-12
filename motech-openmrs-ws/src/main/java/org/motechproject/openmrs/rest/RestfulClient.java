package org.motechproject.openmrs.rest;

import java.net.URI;

import org.codehaus.jackson.JsonNode;

public interface RestfulClient {

	JsonNode getEntityByJsonNode(URI uri) throws HttpException;
	
	JsonNode postForJsonNode(URI url, JsonNode body) throws HttpException;
	
	Void postWithEmptyResponseBody(URI url, JsonNode body) throws HttpException;

	void deleteEntity(URI uri) throws HttpException;
}
