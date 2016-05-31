package org.honton.chas.objectmanager.factory;

import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;

/**
 * A factory of ObjectMapper
 */
public interface ObjectMapperFactory {
	/**
	 * Create an ObjectMapper
	 * 
	 * @return A new ObjectMapper
	 */
	ObjectMapper createObjectMapper();

	public static ObjectMapper createMapper(String name, Class<? extends ObjectMapperFactory> factoryClass) {
		try {
			ObjectMapper objectMapper = factoryClass.newInstance().createObjectMapper()
					.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			if (MR_BEAN != null) {
				objectMapper.registerModule(MR_BEAN);
			}
			return objectMapper;
		} catch (Throwable throwable) {
			LoggerFactory.getLogger(ObjectMapperFactory.class).info(name + " failed to create ObjectMapper", throwable);
			return null;
		}
	}

	static final Module MR_BEAN = createMrBean();

	static Module createMrBean() {
		try {
			return MrBeanModule.class.newInstance();
		} catch (Throwable throwable) {
			LoggerFactory.getLogger(ObjectMapperFactory.class).info("Failed to create MrBean", throwable);
			return null;
		}
	}
}
