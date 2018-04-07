package oracle.apiplatform.policies.GeoIP;

import java.util.List;
import org.json.JSONObject;
import oracle.apiplatform.policies.sdk.validation.AbstractPolicyValidator;
import oracle.apiplatform.policies.sdk.validation.Diagnostic;
import oracle.apiplatform.policies.sdk.validation.Diagnostic.Severity;

@SuppressWarnings("unused")
public class GeoIPValidator extends AbstractPolicyValidator implements GeoIPConstants {
	
	@Override
	public void validateSyntax(JSONObject config, Context context) {
		System.out.println ("validateSyntax: Config="+config.toString() + "\n context="+context.toString());
	}

	@Override
	public void validateSemantics(JSONObject policyConfig, Context context,
			List<Diagnostic> diagnostics) {
		System.out.println ("validateSemantics: Config="+policyConfig.toString() + "\n context="+context.toString());

	}

}