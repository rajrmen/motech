package org.motechproject.openmrs.rest.impl;

import java.net.URI;

import org.codehaus.jackson.JsonNode;
import org.motechproject.openmrs.rest.HttpException;
import org.motechproject.openmrs.rest.RestfulClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestOperations;

@Component
public class SpringRestfulClientImpl implements RestfulClient {
	
	private static final Logger logger = LoggerFactory.getLogger(SpringRestfulClientImpl.class);
	
	private final RestOperations restOperations;
	
	@Autowired
	public SpringRestfulClientImpl(RestOperations restOperations) {
		this.restOperations = restOperations;
	}
	
	interface RestCommand<T> {
		T execute();
	}
	
	private class GetRestCommand implements RestCommand<JsonNode> {
		private URI url;

		GetRestCommand(URI uri) {
			this.url = uri;
		}
		
		@Override
		public JsonNode execute() {
			ResponseEntity<JsonNode> response = restOperations.getForEntity(url, JsonNode.class);
			return response.getBody();
		}
	}
	
	private class PostRestCommand implements RestCommand<JsonNode> {

		private URI url;
		private JsonNode body;

		PostRestCommand(URI uri, JsonNode body) {
			this.url = uri;
			this.body = body;
		}
		
		@Override
		public JsonNode execute() {
			ResponseEntity<JsonNode> response = restOperations.postForEntity(url, body, JsonNode.class);
			return response.getBody();
		}
		
	}

	public JsonNode getEntityByJsonNode(URI uri) throws HttpException {
		return executeCommand(new GetRestCommand(uri));
	}

	private <T> T executeCommand(RestCommand<T> command) throws HttpException {
		try {
			return command.execute();
		} catch(HttpClientErrorException e) {
			logger.warn("Request failed with client error: " + e.getMessage());
			throw new HttpException(e.getMessage());
		} catch(HttpServerErrorException e) {
			logger.warn("Request failed with server error:" + e.getMessage());
			throw new HttpException(e.getMessage());
		} catch(ResourceAccessException e) {
			logger.warn("Request failed with IOException: " + e.getMessage());
			throw new HttpException(e.getMessage());
		}
	}

	@Override
	public JsonNode postForJsonNode(URI uri, JsonNode body) throws HttpException {
		PostRestCommand command = new PostRestCommand(uri, body);
		return executeCommand(command);
	}

	@Override
    public Void postWithEmptyResponseBody(URI url, JsonNode body) throws HttpException {
	    return executeCommand(new PostCommand(url, body));
    }
	
	private class PostCommand implements RestCommand<Void> {

		private URI uri;
		private JsonNode body;

		public PostCommand(URI uri, JsonNode body) {
			this.uri = uri;
			this.body = body;
		}
		
		@Override
        public Void execute() {
			restOperations.postForEntity(uri, body, null);
			return null;
		}
	}

	@Override
    public void deleteEntity(URI uri) throws HttpException {
	    executeCommand(new DeleteCommand(uri));
    }
	
	private class DeleteCommand implements RestCommand<Void> {

		private URI uri;
		
		DeleteCommand(URI uri) {
			this.uri = uri;
		}
		
		@Override
        public Void execute() {
	        restOperations.delete(uri);
	        return null;
        }
		
	}
}
