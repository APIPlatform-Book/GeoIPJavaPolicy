package oracle.apiplatform.policies.GeoIP;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import oracle.apiplatform.policies.GeoIP.GeoIPConstants;
import oracle.apiplatform.policies.sdk.context.ApiRuntimeContext;
import oracle.apiplatform.policies.sdk.exceptions.PolicyProcessingException;
import oracle.apiplatform.policies.sdk.runtime.PolicyRuntime;
import oracle.apiplatform.policies.sdk.runtime.PolicyRuntimeInitContext;

public class GeoIPRuntime implements PolicyRuntime, GeoIPConstants {
	
	final static Logger logger = Logger.getLogger(GeoIPRuntime.class);	

	private static final String TESTHOST = "Test.Host";
	private static boolean test = true;

	private static final int HTTP_ERROR = 403;
	private static final String NOERROR = null;
	private static final String ERRORCODE_NOFILTERDATA = "1102";
	private static final String ERRORCODE_NOORIGINIP = "1103";
	private static final String ERRORCODE_NOLOCATION = "1104";
	private static final String ERRORCODE_FILTEREDORIGIN = "1104";

	// we put the country of origin into this header element
	private static final String ORIGIN_COUNTRY = "Origin.Country";


	private DataFilter filter = null;

	public GeoIPRuntime( PolicyRuntimeInitContext initContext,
			JSONObject policyConfig,
			DataFilter filter) 
	{
		this.filter = filter;		
	}


	/**
	 * @param filter the data filter object that contains the information to convert the IP to a location
	 * @param remoteHost the host IP address from the header
	 * @return
	 * @throws PolicyProcessingException
	 */
	static String applyFilter (DataFilter filter, String remoteHost) throws PolicyProcessingException
	{
		String origin = null;

		LocationDescriptor location= null;
		String errorCode = NOERROR;

		if (remoteHost == null)
		{
			errorCode = ERRORCODE_NOORIGINIP;
		} 
		else
		{

			if (filter != null)
			{
				location = filter.getLocationDescriptor(remoteHost);
			} else
			{
				errorCode = ERRORCODE_NOFILTERDATA;
			}

			if (location != null)
			{
				origin = location.getCountryName();

			} else
			{
				errorCode = ERRORCODE_NOLOCATION;
			}
		}

		// if we have a problem retrieving a location throw an error
		if (errorCode != NOERROR)
		{
			logger.info("GeoIPRuntime error flagged : "+ errorCode+"\n  RemoteHost is:"+remoteHost);
			// for some reason we can't accept the data
			throw new GeoIPRuntimeException(
					errorCode, HTTP_ERROR,
					remoteHost);
		}
		logger.info("GeoIPRuntime reporting location : "+ origin +" for "+remoteHost);
		
		if (location.isExcluded())
		{
			throw new PolicyProcessingException (ERRORCODE_FILTEREDORIGIN, HTTP_ERROR,
					remoteHost);
		}
		
		return origin;

	}

	@Override
	public boolean execute(ApiRuntimeContext apiRuntimeContext) throws PolicyProcessingException {

		String originName = null;
		String originIP = apiRuntimeContext.getApiRequest().getRemoteHost();

		if (test)
		{
			logger.info("will check for override");

			try {
				originIP = apiRuntimeContext.getApiRequest().getHeader(TESTHOST);
				if (originIP != null)
				{
					originIP = originIP.trim();

					if (originIP.length() == 0)
					{
						// defer back to the default value
						originIP = apiRuntimeContext.getApiRequest().getRemoteHost();
						logger.info ("couldn't override origin");
					}
				}
			} 
			catch (IOException ioErr) 
			{
				logger.error("getting origin. msg="+ioErr.getLocalizedMessage(), ioErr);
			}
		}
		
		originName = applyFilter (filter, apiRuntimeContext.getApiRequest().getRemoteHost());

		apiRuntimeContext.getServiceRequest().setHeader(ORIGIN_COUNTRY, originName);
		

		return true;
	}
}