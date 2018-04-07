package oracle.apiplatform.policies.GeoIP;

import oracle.apiplatform.policies.sdk.exceptions.PolicyProcessingException;

/**
 * Separate class so we can differentiate a specific exception relating to  GeoIP vs a general rejection of the API call
 */
public class GeoIPRuntimeException extends PolicyProcessingException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4027653755332075378L;

	public GeoIPRuntimeException(String id, int httpCode, String message) {
		super(id, httpCode, message);
	}
	
	

}
