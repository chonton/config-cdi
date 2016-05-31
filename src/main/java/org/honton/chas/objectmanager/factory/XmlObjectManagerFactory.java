package org.honton.chas.objectmanager.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * Factory for Jackson ObjectMapper which understands xml format.
 * This method is in separate class so that xml databind jar can be optional
 */
public class XmlObjectManagerFactory implements ObjectMapperFactory {
	@Override
	public ObjectMapper createObjectMapper() {
		return new XmlMapper();
	}
}
