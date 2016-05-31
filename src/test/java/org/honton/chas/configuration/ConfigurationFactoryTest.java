package org.honton.chas.configuration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalPackages;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

@RunWith(CdiRunner.class)
@AdditionalPackages(ConfigurationCache.class)
public class ConfigurationFactoryTest {
	
	@Inject
	ConfigurationCacheFactory factory;
	
	@Inject @ConfigurationSource("classpath:/org/honton/chas/configuration/json-config-bean.json")
	private ConfigurationCache<ConfigBean> absolute;

	@Test
	public void testAbsoluteResourceName() throws Exception {
		Assert.assertEquals("json string value", absolute.get().getString());
	}
	
	private InjectionPoint createMockInjectionPoint(String uri) throws NoSuchFieldException {
		ConfigurationSource configurationSource = Mockito.mock(ConfigurationSource.class);
		Mockito.when(configurationSource.value()).thenReturn(uri);

		Annotated annotated = Mockito.mock(Annotated.class);
		Mockito.when(annotated.getAnnotation(Mockito.eq(ConfigurationSource.class))).thenReturn(configurationSource);
		
		InjectionPoint injectionPoint = Mockito.mock(InjectionPoint.class);
		Field field = getClass().getDeclaredField("absolute");
		Mockito.when(injectionPoint.getType()).thenReturn(field.getGenericType());
		Mockito.when(injectionPoint.getMember()).thenReturn(field);
		Mockito.when(injectionPoint.getAnnotated()).thenReturn(annotated);
		return injectionPoint;
	}
	
	String getFileSource() throws URISyntaxException {
		URI locationUri = getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
		String fileName = ConfigBean.class.getPackage().getName().replace('.', '/')+"/json-config-bean.json";
		File configFile= new File(new File(locationUri), fileName);
		return configFile.toURI().toString();		
	}
	
	@Test
	public void testReadFileUrl() throws Exception {
		InjectionPoint injectionPoint = createMockInjectionPoint(getFileSource());		
		ConfigurationCache<ConfigInterface> cache = factory.getCache(injectionPoint);
		ConfigInterface bean = cache.get();
		Assert.assertEquals("json string value", bean.getString());
		Assert.assertEquals(15L, bean.getLongValue().longValue());
	}
	
	@Test
	public void testCachingOccurs() throws Exception {
		InjectionPoint injectionPoint = createMockInjectionPoint(getFileSource());		
		ConfigurationCache<ConfigInterface> one = factory.getCache(injectionPoint);
		ConfigurationCache<ConfigInterface> two = factory.getCache(injectionPoint);
		Assert.assertSame(one, two);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testNoResourceName() throws Exception {
		InjectionPoint injectionPoint = createMockInjectionPoint(ConfigurationCacheFactory.CLASSPATH_SCHEMA);
		factory.getCache(injectionPoint);
	}
	
	@Test(expected = IOException.class)
	public void testUnknownResourceName() throws Exception {
		InjectionPoint injectionPoint = createMockInjectionPoint(ConfigurationCacheFactory.CLASSPATH_SCHEMA + "unknown.json");
		factory.getCache(injectionPoint);
	}
	
}
