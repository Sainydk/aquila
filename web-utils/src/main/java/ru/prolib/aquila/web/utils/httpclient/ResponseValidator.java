package ru.prolib.aquila.web.utils.httpclient;

import org.apache.http.client.methods.CloseableHttpResponse;

import ru.prolib.aquila.web.utils.InvalidResponseException;

public interface ResponseValidator {
	
	/**
	 * Validate response, throw an exception if not valid.
	 * <p>
	 * @param response - the response to validate
	 * @throws InvalidResponseException - invalid response
	 */
	public void validateResponse(CloseableHttpResponse response)
			throws InvalidResponseException;

}
