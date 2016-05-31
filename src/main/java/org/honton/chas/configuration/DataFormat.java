package org.honton.chas.configuration;

import org.honton.chas.objectmanager.factory.ConfObjectManagerFactory;
import org.honton.chas.objectmanager.factory.JsonObjectManagerFactory;
import org.honton.chas.objectmanager.factory.ObjectMapperFactory;
import org.honton.chas.objectmanager.factory.XmlObjectManagerFactory;
import org.honton.chas.objectmanager.factory.YamlObjectManagerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

/**
 * The data formats supported by different ObjectMappers.
 */
@Getter
public enum DataFormat {

	JSON(JsonObjectManagerFactory.class),
	CONF(ConfObjectManagerFactory.class),
	YAML(YamlObjectManagerFactory.class),
	XML(XmlObjectManagerFactory.class);

	private final String extension;
	private final ObjectMapper objectMapper;

	DataFormat(Class<? extends ObjectMapperFactory> factory) {
		extension = name().toLowerCase();
		objectMapper = ObjectMapperFactory.createMapper(name(), factory);
	}

	public static DataFormat of(String extension) {
		for(DataFormat dataFormat : values()) {
			if(dataFormat.extension.equals(extension)) {
				return dataFormat;
			}
		}
		throw new IllegalArgumentException("extension '"+extension+ "' not supported");
	}
}
