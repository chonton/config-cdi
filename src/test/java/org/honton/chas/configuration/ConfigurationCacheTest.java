package org.honton.chas.configuration;

import java.net.URL;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfigurationCacheTest {
	
	private ConfigurationCache<ConfigBean> configBean;

	private void createConfigCache(URL sourceUrl) throws Exception {
		ObjectMapper objectMapper = Mockito.spy(DataFormat.JSON.getObjectMapper());
		JavaType javaType = objectMapper.getTypeFactory().constructType(ConfigBean.class);
		ConfigBean expected1 = new ConfigBean();
		expected1.setLongValue(1L);
		ConfigBean expected2 = new ConfigBean();
		expected2.setLongValue(2L);
		Mockito.doReturn(expected1)
			.doReturn(expected2)
			.when(objectMapper).readValue(Mockito.eq(sourceUrl), Mockito.eq(javaType));
		configBean = new ConfigurationCache<>(ConfigBean.class, objectMapper, sourceUrl, 500);
	}

	@Test
	public void testFileIsSet() throws Exception {
		URL sourceUrl = getClass().getResource("json-config-bean.json");
		createConfigCache(sourceUrl);
		Assert.assertEquals("json-config-bean.json", configBean.getFile().getName());
	}
	
	@Test
	public void testJarFileIsSet() throws Exception {
		createConfigCache(new URL("jar:file:///lib/some.jar!/some.class"));
		Assert.assertEquals("/lib/some.jar", configBean.getFile().getAbsolutePath());
	}
	
	@Test
	public void testJarFileNotSet() throws Exception {
		createConfigCache(new URL("jar:http://localhost/lib/some.jar!/some.class"));
		Assert.assertNull(configBean.getFile());
	}
	
	@Test
	public void testHttpFileNotSet() throws Exception {
		createConfigCache(new URL("http://localhost/lib/some.json"));
		Assert.assertNull(configBean.getFile());
	}
	
	@Test
	public void testHttpRefreshCalled() throws Exception {
		createConfigCache(new URL("http://localhost/lib/some.json"));
		Assert.assertSame(1L, configBean.get().getLongValue());	
		Thread.sleep(1000L);
		Assert.assertSame(2L, configBean.get().getLongValue());	
	}
	
	@Test
	public void testRefreshCalled() throws Exception {
		URL sourceUrl = getClass().getResource("json-config-bean.json");
		createConfigCache(sourceUrl);
		Assert.assertSame(1L, configBean.get().getLongValue());	
		Thread.sleep(1000L);
		// file url should not be re-read until after file touch
		Assert.assertSame(1L, configBean.get().getLongValue());	
		
		configBean.getFile().setLastModified(System.currentTimeMillis());
		Thread.sleep(1000L);
		// file should be reloaded after file touch
		Assert.assertSame(2L, configBean.get().getLongValue());	
	}

}
