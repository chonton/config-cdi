package org.honton.chas.configuration;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;

@Getter(AccessLevel.PACKAGE)
public class ConfigurationCache<T> {

	private final JavaType type;
	private final ObjectMapper objectMapper;
	private final URL sourceUrl;
	private final File file;
	private final long checkInterval;
	private final Lock lock;
	
	private T bean;
	private long lastCheckTime;
	private long lastFileTime;

	ConfigurationCache(Type type, ObjectMapper objectMapper, URL sourceUrl, long checkInterval) {
		this.type = objectMapper.getTypeFactory().constructType(type);
		this.objectMapper = objectMapper;
		this.sourceUrl = sourceUrl;
		this.file = getFile(sourceUrl);
		this.checkInterval = checkInterval;
		lock = new ReentrantLock();
		get();
	}

	@SneakyThrows
	public T get() {
		if (shouldRefresh()) {
			bean = objectMapper.readValue(sourceUrl, this.type);
		}
		return bean;
	}

	private boolean shouldRefresh() {
		lock.lock();
		try {
			long currentTime = System.currentTimeMillis();
			boolean notExpired = currentTime - lastCheckTime < checkInterval;
			lastCheckTime = currentTime;
			if (notExpired) {
				return false;
			}
			if (file == null) {
				return true;
			}
			long currentFileTime = file.lastModified();
			boolean isOld = lastFileTime < currentFileTime;
			lastFileTime = currentFileTime;
			return isOld;
		}
		finally {
			lock.unlock();
		}
	}

	@SneakyThrows
	private static File getFile(URL sourceUrl) {
		String scheme = sourceUrl.getProtocol();
		if ("jar".equals(scheme)) {
			String fileName = sourceUrl.getFile();
			int colon = fileName.indexOf(':');
			scheme = fileName.substring(0, colon);
			if ("file".equals(scheme)) {
				int bang = fileName.indexOf('!');
				return new File(fileName.substring(colon + 1, bang));
			}
		} else if ("file".equals(scheme)) {
			return new File(sourceUrl.toURI());
		}
		return null;
	}
}
