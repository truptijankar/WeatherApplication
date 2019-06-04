package com.weather.application.model;

/**
 * @author trupti.jankar 
 * This is Error class for handling custom errors
 */

public class CustomError {

	private long statusCode;

	private Exception customException;

	private String errorMessage;

	public long getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(long statusCode) {
		this.statusCode = statusCode;
	}

	public Exception getCustomException() {
		return customException;
	}

	public void setCustomException(Exception customException) {
		this.customException = customException;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
