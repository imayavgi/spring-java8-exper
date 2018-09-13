package uiak.exper;

import org.apache.commons.lang3.StringUtils;

import org.junit.Test;

public class StringUtilExperTest {

	@Test
	public void testCompanyNames() {
		double jaroWinklerDistance = StringUtils.getJaroWinklerDistance("Johnson and Johnson", "Johnson & Johnson");
		System.err.println(jaroWinklerDistance);
	}

}
