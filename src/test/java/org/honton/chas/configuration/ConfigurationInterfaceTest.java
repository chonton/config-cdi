package org.honton.chas.configuration;

import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalPackages;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CdiRunner.class)
@AdditionalPackages(ConfigurationCache.class)
public class ConfigurationInterfaceTest {
	
	@Inject @ConfigurationSource("classpath:json-config-bean.json")
	private ConfigurationCache<ConfigInterface> interfaceBean;

	@Test
	public void testInterface() throws Exception {
		ConfigInterface configInterface = interfaceBean.get();
		Assert.assertEquals("json string value", configInterface.getString());
		Assert.assertEquals(15L, configInterface.getLongValue().longValue());
	}
}
