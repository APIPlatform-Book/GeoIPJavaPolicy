package oracle.apiplatform.policies.GeoIP;

import org.json.JSONObject;

import oracle.apiplatform.policies.sdk.runtime.PolicyRuntime;
import oracle.apiplatform.policies.sdk.runtime.PolicyRuntimeFactory;
import oracle.apiplatform.policies.sdk.runtime.PolicyRuntimeInitContext;

public class GeoIPRuntimeFactory implements PolicyRuntimeFactory {

	private static DataFilter dataFilter = null;


	@Override
	public PolicyRuntime getRuntime(PolicyRuntimeInitContext initContext,
			JSONObject policyConfig) throws Exception {

		if (dataFilter == null) {
			initializeFilterData(policyConfig);
		}

		return new GeoIPRuntime(initContext, policyConfig, dataFilter);

	}

	static void initializeFilterData(JSONObject policyConfig) {

		if (dataFilter == null)
		{
			dataFilter = new DataFilter ((String) policyConfig.get(GeoIPConstants.COUNTRY), 
					(String) policyConfig.get(GeoIPConstants.NETWORKFILENAME),
					(String) policyConfig.get(GeoIPConstants.GEOFILENAME));
		}

	}	

}