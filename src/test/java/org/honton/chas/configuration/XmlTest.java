package org.honton.chas.configuration;

import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalPackages;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CdiRunner.class)
@AdditionalPackages(ConfigurationCache.class)
public class XmlTest {
	
	@Inject
	@ConfigurationSource("classpath:xml-config-bean.xml")
	ConfigurationCache<ConfigBean> xml;
	
	@Test
	public void testXml() {
		ConfigBean bean = xml.get();
		Assert.assertEquals("xml string value", bean.getString());
		Assert.assertEquals(15L, bean.getLongValue().longValue());
	}	
}
