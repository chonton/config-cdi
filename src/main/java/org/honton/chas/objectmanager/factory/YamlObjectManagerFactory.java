package org.honton.chas.objectmanager.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * Factory for Jackson ObjectMapper which understands yaml format.
 * This method is in separate class so that yaml databind jar can be optional
 */
public class YamlObjectManagerFactory implements ObjectMapperFactory {
	@Override
	public ObjectMapper createObjectMapper() {
		return new ObjectMapper(new YAMLFactory());
	}
}
