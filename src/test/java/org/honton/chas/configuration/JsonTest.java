package org.honton.chas.configuration;

import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalPackages;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CdiRunner.class)
@AdditionalPackages(ConfigurationCache.class)
public class JsonTest {
	
	@Inject
	@ConfigurationSource("classpath:json-config-bean.json")
	ConfigurationCache<ConfigBean> json;
	
	@Test
	public void testJson() {
		ConfigBean bean = json.get();
		Assert.assertEquals("json string value", bean.getString());
		Assert.assertEquals(15L, bean.getLongValue().longValue());
	}
}
