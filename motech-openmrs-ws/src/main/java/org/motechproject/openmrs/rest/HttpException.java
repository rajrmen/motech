package org.motechproject.openmrs.rest;

public class HttpException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public HttpException(String message) {
		super(message);
	}

}
