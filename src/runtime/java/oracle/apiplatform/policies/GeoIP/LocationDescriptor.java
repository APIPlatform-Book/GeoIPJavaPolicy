package oracle.apiplatform.policies.GeoIP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import org.apache.log4j.Logger;

public class LocationDescriptor {

	final static Logger logger = Logger.getLogger(LocationDescriptor.class);

	public static final int GEONAME = 0;
	public static final int COUNTRYNAME = 1;
	public static final int COUNTRYCODE = 2;
	public static final int CONTINENTCODE = 3;
	public static final int CONTINENTNAME = 4;
	public static final int LOCALECODE = 5;

	private String countryName = null;
	private String countryCode = null;
	private String continentCode = null;
	private String continentName = null;
	private String geoName = null;
	private String localeCode = null;

	private boolean excluded = false;

	public static HashMap<String, LocationDescriptor> buildDescriptorList(ArrayList<String[]> rawData,
			HashMap<Integer, Integer> mapping, ArrayList<String> exclusions) 
	{
		HashMap<String, LocationDescriptor> locationDescriptorList = new HashMap<String, LocationDescriptor>();

		logger.debug("About to build " + rawData.size() + " locations;\n mapping set is:" + mapping.toString());

		ListIterator<String[]> rawIter = rawData.listIterator();

		while (rawIter.hasNext()) 
		{
			String[] values = (String[]) rawIter.next();
			LocationDescriptor instance = new LocationDescriptor(values, mapping, exclusions);
			locationDescriptorList.put(instance.getGeoName(), instance);
		}

		return locationDescriptorList;
	}

	public LocationDescriptor(String countryName, String continentName) {
		this.countryName = countryName;
		this.continentName = continentName;
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


	public LocationDescriptor(String[] values, HashMap<Integer, Integer> mapping, ArrayList<String> exclusions) 
	{

		// as some records may not be complete - process what we can but dont blow up trying to read a column that hasn't been captured
		try
		{
			if ((mapping.get(GEONAME) != null) && (values.length < mapping.get(GEONAME)))
			{
				geoName = values[mapping.get(GEONAME)];
			}

			if ((mapping.get(COUNTRYNAME) != null) && (values.length < mapping.get(COUNTRYNAME)))
			{
				countryName = values[mapping.get(COUNTRYNAME)];
			}
			if ((mapping.get(COUNTRYCODE) != null) && (values.length < mapping.get(COUNTRYCODE)))
			{
				countryCode = values[mapping.get(COUNTRYCODE)];
			}
			if ((mapping.get(CONTINENTCODE) != null) && (values.length < mapping.get(CONTINENTCODE)))
			{
				continentCode = values[mapping.get(CONTINENTCODE)];
			}
			if ((mapping.get(CONTINENTNAME) != null) && (values.length < mapping.get(CONTINENTNAME)))
			{
				continentName = values[mapping.get(CONTINENTNAME)];
			}
			if ((mapping.get(LOCALECODE) != null) && (values.length < mapping.get(LOCALECODE)))
			{
				localeCode = values[mapping.get(LOCALECODE)];
			}
		}
		catch (ArrayIndexOutOfBoundsException mappingErr)
		{
			// we've seen in the data that not all columns maybe populated  - as a result 
			logger.error ("LocationDescriptor creating partial object :"+prettyFormat(values) + " failed because " + mappingErr.getMessage() + "\n mapping is:" + mapping, mappingErr);			
		}

		setExclusion(exclusions);

		if (logger.isInfoEnabled()) // dont do lots of string manipulation if not needed
		{
			logger.debug("LocationDescriptor creating with:"+prettyFormat(values));
		}
	}

	/**
	 * This bit of logic has been separated out as we may wish to extend it to take into account the user may use the ISO codes or alternate names
	 * @param exclusions
	 */
	private void setExclusion(ArrayList<String> exclusions) 
	{
		if (exclusions.contains(countryName)) 
		{
			excluded = true;
		}
	}

	public String getGeoName() {
		return geoName;
	}

	public String getLocaleCode() {
		return localeCode;
	}

	public String getCountryName() {
		return countryName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public String getContinentCode() {
		return continentCode;
	}

	public String getContinentName() {
		return continentName;
	}

	public boolean isExcluded() {
		return excluded;
	}

	public String toString()
	{
		return "Country Name = " +countryName + "\n" + 
				"Country Code = "+ countryCode + "\n"+
				"Continent Code = " + continentCode + "\n"+
				"Continent Name = " + continentName + "\n"+
				"Geo Name = " + geoName + "\n"+
				"Locale Code = " + localeCode;
	}
}
