package org.honton.chas.configuration;

import org.honton.chas.configuration.DataFormat;
import org.junit.Assert;
import org.junit.Test;

public class DataFormatTest {

	@Test
	public void testIgnoreCase() {
		Assert.assertEquals(DataFormat.JSON, DataFormat.of("json"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotSupported() {
		DataFormat.of("xyz");
	}
}
