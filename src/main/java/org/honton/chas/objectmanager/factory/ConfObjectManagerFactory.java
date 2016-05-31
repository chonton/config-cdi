package org.honton.chas.objectmanager.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasonclawson.jackson.dataformat.hocon.HoconFactory;

/**
 * Factory for Jackson ObjectMapper which understands hocon format.
 * This method is in separate class so that hocon databind jar can be optional
 */
public class ConfObjectManagerFactory implements ObjectMapperFactory {
	@Override
	public ObjectMapper createObjectMapper() {
		return new ObjectMapper(new HoconFactory());
	}
}
