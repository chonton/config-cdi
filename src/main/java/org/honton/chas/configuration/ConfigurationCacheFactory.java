package org.honton.chas.configuration;

import java.io.IOException;
import java.lang.reflect.Member;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class ConfigurationCacheFactory {
	private final ConcurrentMap<Key, ConfigurationCache<?>> caches = new ConcurrentHashMap<>();

	/**
	 * Create a ConfigurationCache from an InjectionPoint.  The injectionPoint must be annotated with a 
	 * {@link ConfigurationSource}.  A 'classpath' URI value will search the classpath for matching resources.  
	 * Relative 'classpath' URIs are resolved relative to the class being injected.
	 * 
	 * @param injectionPoint
	 *            The information about an injected member
	 * @return The ConfigurationCache
	 */
	@Produces
	@ConfigurationSource("")
	<T> ConfigurationCache<T> getCache(InjectionPoint injectionPoint) {
		Key key = new Key(injectionPoint);
		@SuppressWarnings("unchecked")
		ConfigurationCache<T> cache = (ConfigurationCache<T>) caches.get(key);
		if (cache == null) {
			synchronized (caches) {
				cache = new CacheCreator(key, injectionPoint).createCache();
				caches.put(key, cache);
			}
		}
		return cache;
	}

	@Getter
	@EqualsAndHashCode
	private static class Key {
		private final String source;
		private final Type type;

		Key(InjectionPoint injectionPoint) {
			source = getConfigurationSource(injectionPoint).value();
			type = ((ParameterizedType) injectionPoint.getType()).getActualTypeArguments()[0];
		}
	}

	private static class CacheCreator {
		private final Key key;
		private final InjectionPoint injectionPoint;
		private URL matchUrl;
		private ObjectMapper objectMapper;

		private CacheCreator(Key key, InjectionPoint injectionPoint) {
			this.key = key;
			this.injectionPoint = injectionPoint;
		}

		private Class<?> getDeclaringClass() {
			Member member = injectionPoint.getMember();
			return member.getDeclaringClass();
		}

		private URL getMatchUrl(String source) throws IOException {
			if (source.startsWith(CLASSPATH_SCHEMA)) {
				if (source.length() == CLASSPATH_SCHEMA_OFFSET) {
					throw new IllegalArgumentException("classpath uri must include resource name");
				}
				return getMatchUrlFromResource(source.substring(CLASSPATH_SCHEMA_OFFSET));
			} else {
				return new URL(source);
			}
		}

		private URL getMatchUrlFromResource(String resource) throws IOException {
			Class<?> declaringClass = getDeclaringClass();
			if(resource.charAt(0) == '/') {
				resource = resource.substring(1);
			}
			else {
				resource = declaringClass.getPackage().getName().replace('.', '/') + '/' + resource;
			}
			URL matchUrl = null;
			for (Enumeration<URL> urls = declaringClass.getClassLoader().getResources(resource); urls.hasMoreElements();) {
				URL url = urls.nextElement();
				if (matchUrl == null) {
					matchUrl = url;
				} else {
					log.warn("multiple matches in classpath, ignoring " + url);
				}
			}
			if (matchUrl == null) {
				throw new IOException("resource '" + resource + " not found in classpath");
			}
			return matchUrl;
		}

		@SneakyThrows
		private void setObjectMapperAndUrl(String path) {
			int solidus = path.lastIndexOf('/') + 1;
			int period = path.lastIndexOf('.') + 1;
			if(solidus >= period) {
				throw new IllegalArgumentException("extension required to determine data format");
			}
			DataFormat df = DataFormat.of(path.substring(period));
			objectMapper = df.getObjectMapper();
			if (objectMapper == null) {
				throw new UnsupportedOperationException(df + " does not have dependencies in classpath");
			}
			
			matchUrl = getMatchUrl(path);
		}

		private <T> ConfigurationCache<T> createCache() {
			setObjectMapperAndUrl(key.getSource());
			return new ConfigurationCache<T>(key.getType(), objectMapper, matchUrl, getConfigurationSource(injectionPoint).interval());
		}
	}

	private static ConfigurationSource getConfigurationSource(InjectionPoint injectionPoint) {
		return injectionPoint.getAnnotated().getAnnotation(ConfigurationSource.class);
	}

	static final String CLASSPATH_SCHEMA = "classpath:";
	static private final int CLASSPATH_SCHEMA_OFFSET = CLASSPATH_SCHEMA.length();
}
