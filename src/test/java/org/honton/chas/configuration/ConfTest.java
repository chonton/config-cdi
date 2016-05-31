package org.honton.chas.configuration;

import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalPackages;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CdiRunner.class)
@AdditionalPackages(ConfigurationCache.class)
public class ConfTest {
	
	@Inject
	@ConfigurationSource("classpath:conf-config-bean.conf")
	ConfigurationCache<ConfigBean> hocon;
	
	@Test
	public void testConf() {
		ConfigBean bean = hocon.get();
		Assert.assertEquals("hocon string value", bean.getString());
		Assert.assertEquals(15L, bean.getLongValue().longValue());
	}
}
