package oracle.apiplatform.policies.GeoIP;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;


public class DataFilter {

	final static Logger logger = Logger.getLogger(DataFilter.class);


	private static final String SEPARATOR = ",";
	private static final String FILESEPARATOR = ",";

	private HashMap<String, LocationDescriptor> locationDescriptorList = null;
	private ArrayList<NetworkDescriptor> networkList = null;

	/**
	 * Gets the mapping for files to columns.  If the source file is different we just override this function
	 * @return Hashmap with a mapping of values to file columns
	 */
	HashMap<Integer, Integer> getNetworkMapping()
	{
		HashMap<Integer, Integer> networkFileMapping = new HashMap<Integer, Integer> ();

		networkFileMapping.put (NetworkDescriptor.NETWORK, 0);
		networkFileMapping.put (NetworkDescriptor.GEONAMEID, 1);
		networkFileMapping.put (NetworkDescriptor.ANONPROXY,4);
		networkFileMapping.put (NetworkDescriptor.SATPROVIDER, 5);

		logger.debug(networkFileMapping.toString());
		return networkFileMapping;
	}

	HashMap<Integer, Integer> getLocationMapping()
	{
		HashMap<Integer, Integer> locationMapping = new HashMap<Integer, Integer> ();

		locationMapping.put (LocationDescriptor.GEONAME,  0);
		locationMapping.put (LocationDescriptor.COUNTRYNAME,5);
		locationMapping.put (LocationDescriptor.COUNTRYCODE,4);
		locationMapping.put (LocationDescriptor.CONTINENTCODE, 2);
		locationMapping.put (LocationDescriptor.CONTINENTNAME, 3);
		locationMapping.put (LocationDescriptor.LOCALECODE,1);

		logger.debug(locationMapping.toString());	    
		return locationMapping;
	}


	public DataFilter (String exclusionsList, String networkFile, String locationsFile) 
	{

		if (logger.isInfoEnabled()) // test first as we're building strings a lot 
		{
			logger.info("creation request with:\n network file:" + networkFile + "\n locationfile:"+locationsFile+"\n exclusions:"+exclusionsList);
		}

		ArrayList<String> exclusions = buildExclusionData(exclusionsList);
		ArrayList<String[]> NetworkToGeoName = readCSV(networkFile);
		ArrayList<String[]> rawGeoNameToLocationDesc = readCSV(locationsFile);

		locationDescriptorList = LocationDescriptor.buildDescriptorList (rawGeoNameToLocationDesc, getLocationMapping(), exclusions);
		networkList = NetworkDescriptor.buildNetworkList (NetworkToGeoName, getNetworkMapping());

	}

	/**
	 * @param IP needing to be located expected to be in the notation of a.b.c.d e.g 1.0.1.0
	 * @return boolean indicating whether the location is considered to be in the exclusion list
	 */
	public boolean applyFilter (String IP)
	{
		boolean filterOut = false;

		// find which network this IP belongs to
		Iterator<NetworkDescriptor> iter = networkList.iterator();

		while (iter.hasNext())
		{
			NetworkDescriptor network = iter.next();

			if (network.isInNetwork(IP))
			{
				LocationDescriptor location= locationDescriptorList.get(network.getGeoNameId());
				if (location != null)
				{
					filterOut = location.isExcluded();
					logger.debug("apply filter matched for " + location.toString() + " reporting " + filterOut);
				}
				return filterOut;
			}
		}

		logger.debug("No match for " + IP + " not excluding");
		return filterOut;
	}

	/**
	 * @param IP needing to be located expected to be in the notation of a.b.c.d e.g 1.0.1.0
	 * @return Location description object that relates to the location of this IP
	 */
	public LocationDescriptor getLocationDescriptor (String IP)
	{
		LocationDescriptor location = null;

		// find which network this IP belongs to
		Iterator<NetworkDescriptor> iter = networkList.iterator();

		while (iter.hasNext() && (location==null))
		{
			NetworkDescriptor network = iter.next();

			if (network.isInNetwork(IP))
			{

				logger.debug("looking up " + network.getGeoNameId());
				location = locationDescriptorList.get(network.getGeoNameId());

				return location;
			}
		}

		return location;
	}

	/**
	 * @param policyConfig
	 */
	private static ArrayList<String[]> readCSV(String filename) 
	{
		ArrayList<String[]> lines = new ArrayList<String[]>();

		filename = filename.trim();
		if ((filename != null) && (filename.length() > 0)) 
		{
			BufferedReader br = null;
			String line = "";
			String cvsSplitBy = FILESEPARATOR;
			try {
				br = new BufferedReader(new FileReader(filename));
				while ((line = br.readLine()) != null) 
				{

					// use comma as separator
					String[] lineElements = line.split(cvsSplitBy);

					lines.add(lineElements);
				}

			} 
			catch (FileNotFoundException fileErr) 
			{
				logger.error(fileErr.getMessage(), fileErr);
			} 
			catch (IOException ioErr) 
			{
				logger.error(ioErr.getMessage(), ioErr);
			} 
			finally 
			{
				logger.debug("tidying up");
				if (br != null) 
				{
					try {
						br.close();
					} catch (IOException finalErr) 
					{
						logger.error(finalErr.getMessage(), finalErr);
					}
				} // if
			} // finally
		} // file have file

		logger.debug("just read " + lines.size() + " from " + filename);
		return lines;
	}

	/**
	 * Takes the countries to exclude in its user provided form and translates it to
	 * a clean ArrayList which can then be used to determine a lookup
	 * 
	 * @param rawExclusions
	 *            string separated by SEPARATOR
	 * 
	 */
	private static ArrayList<String> buildExclusionData(String rawExclusions) {
		ArrayList<String> exclusions = new ArrayList<String>();

		logger.debug("Preparing exclusdions:"+rawExclusions);
		// initialise the filter data object - which should be a comma separated list
		rawExclusions = rawExclusions.trim();

		// if our comma separated list has meaningful content then process it
		if ((rawExclusions != null) && (rawExclusions.length() > 0)) {
			// split the list
			String[] exclude = rawExclusions.split(SEPARATOR);

			// go trough ad remove any redundant whitespace.
			// dispose of the separator characters
			for (int idx = 0; idx < exclude.length; idx++) 
			{
				exclude[idx].replaceAll(SEPARATOR, " ");
				exclude[idx] = exclude[idx].trim();

				// if we've something left after tidying up then add it to our exclusion list
				if (exclude[idx].length() > 0) 
				{
					exclusions.add(exclude[idx]);
					logger.debug("Added exclusion>"+exclude[idx]+"<");
				}
			}

		}

		return exclusions;
	}
}
