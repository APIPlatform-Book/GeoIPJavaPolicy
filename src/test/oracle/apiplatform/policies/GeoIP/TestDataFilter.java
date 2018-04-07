package oracle.apiplatform.policies.GeoIP;

import static org.junit.Assert.*;

import org.junit.Test;

import groovy.util.logging.Log4j;

import org.apache.log4j.*;

import oracle.apiplatform.policies.GeoIP.DataFilter;

public class TestDataFilter {


	//@Test
	public void testDataFilter() {
		BasicConfigurator.configure();

		DataFilter testDataFilter = new DataFilter ("Syria", "/home/oracle/country-small.csv", "/home/oracle/locations.csv");

		assertNotNull(testDataFilter);
	}

	//@Test
	public void testApplyFilter() {
		org.apache.log4j.BasicConfigurator.configure();

		DataFilter testDataFilter = new DataFilter ("Syria", "/home/oracle/country-small.csv", "/home/oracle/locations.csv");
		assertNotNull(testDataFilter);

		try
		{
			boolean result = testDataFilter.applyFilter("1.0.0.1");
			assertTrue(result);
		}catch (Exception err)
		{
			fail (err.getMessage());
		}

	}

	@Test
	public void testGetLocationDescriptor() 
	{		
		org.apache.log4j.BasicConfigurator.configure();

		DataFilter testDataFilter = new DataFilter ("Syria", "/home/oracle/country-small.csv", "/home/oracle/locations.csv");
		assertNotNull(testDataFilter);

		try
		{
			LocationDescriptor result = null;
			result = testDataFilter.getLocationDescriptor ("1.0.0.1");
			System.out.println ("got descriptior = " +(result == null));
			assertTrue(result != null);
		}catch (Exception err)
		{
			fail (err.getMessage());
		}


	}

}
