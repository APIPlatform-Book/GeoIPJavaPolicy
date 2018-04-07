package oracle.apiplatform.policies.GeoIP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import org.apache.commons.net.util.SubnetUtils;
import org.apache.log4j.Logger;

public class NetworkDescriptor {



	final static Logger logger = Logger.getLogger(NetworkDescriptor.class);

	private static final String ONE = "1";
	private static final String TRUE = "true";

	public static final int NETWORK = 0;
	public static final int GEONAMEID = 1;
	public static final int ANONPROXY = 2;
	public static final int SATPROVIDER = 3;

	private String geoNameId = null;
	private boolean isSatelliteProvider = false;
	private boolean isAnonProxy = false;
	private SubnetUtils netInfo = null;

	public static ArrayList<NetworkDescriptor> buildNetworkList(ArrayList<String[]> rawData, HashMap<Integer, Integer> mapping) 
	{
		logger.debug("buildNetworkList been given " + rawData.size() + " network entries");
		ArrayList<NetworkDescriptor> NetworkList = new ArrayList<NetworkDescriptor>();
		ListIterator<String[]> rawIter = rawData.listIterator();

		while (rawIter.hasNext()) 
		{
			String[] values = (String[]) rawIter.next();
			NetworkDescriptor newDescriptor = createDescriptor(values, mapping);
			if (newDescriptor != null)
			{
				NetworkList.add(newDescriptor);
			}
		}

		return NetworkList;
	}

	static String prettyFormat (String[] values)
	{
		StringBuffer out = new StringBuffer();
		if (values != null)
		{
			for (int idx = 0; idx < values.length; idx++)
			{
				out.append(values[idx]);
				out.append("|");
			}
		}

		return out.toString();
	}	

	private static NetworkDescriptor createDescriptor(String[] rawdata, HashMap<Integer, Integer> mapping) 
	{
		NetworkDescriptor newDescriptor = null;

		// as we may have a header line or an incomplete row - protect ourselves from a throwable blowing back
		try
		{
			String network = rawdata[mapping.get(NETWORK)];
			String geoNameId = rawdata[mapping.get(GEONAMEID)];

			String anonProxyStr = rawdata[mapping.get(ANONPROXY)];
			String satProviderStr = rawdata[mapping.get(SATPROVIDER)];
			boolean anonProxy = false;
			boolean satProvider = false;

			if (anonProxyStr != null) {
				if ((anonProxyStr.equalsIgnoreCase(TRUE)) || (anonProxyStr.contentEquals(ONE))) {
					anonProxy = true;
				}

			}

			if (satProviderStr != null) {
				if ((satProviderStr.equalsIgnoreCase(TRUE)) || (satProviderStr.contentEquals(ONE))) {
					satProvider = true;
				}
			}

			newDescriptor = new NetworkDescriptor(network, geoNameId, anonProxy, satProvider);
		}
		catch (Exception err)
		{
			logger.error("Error creating NetworkDescriptor with " + prettyFormat(rawdata), err);
		}
		return newDescriptor;
	}

	public NetworkDescriptor(String cidrNotation, String geoNameId, boolean anonProxy, boolean satProvider) 
	{	
		netInfo = new SubnetUtils(cidrNotation);

		this.geoNameId = geoNameId;
		this.isAnonProxy = anonProxy;
		this.isSatelliteProvider = satProvider;

		if (logger.isDebugEnabled())
		{
			logger.debug("creating NetworkDescriptor" + cidrNotation + "|" + geoNameId + "|" + anonProxy + "|" + satProvider +  "|" + netInfo.toString());		
		}
	}

	public String getGeoNameId() {
		return geoNameId;
	}

	public boolean isSatelliteProvider() {
		return isSatelliteProvider;
	}

	public boolean isAnonProxy() {
		return isAnonProxy;
	}

	public boolean isInNetwork(String ip)
	{
		SubnetUtils.SubnetInfo info = netInfo.getInfo();
		boolean isInRange = info.isInRange(ip);


		if (logger.isDebugEnabled())
		{
			logger.debug("evaluating " + ip + " against " + info.getCidrSignature() + " answer " + isInRange);
		}
		return isInRange;
	}

}
