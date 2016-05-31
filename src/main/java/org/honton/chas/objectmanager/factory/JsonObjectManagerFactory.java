package org.honton.chas.objectmanager.factory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Factory for Jackson ObjectMapper which understands json format.
 * This method is in separate class so that other databind jars can be optional
 */
public class JsonObjectManagerFactory implements ObjectMapperFactory {
	@Override
	public ObjectMapper createObjectMapper() {
		return new ObjectMapper();
	}
}
