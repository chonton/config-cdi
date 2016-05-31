package org.honton.chas.configuration;

import lombok.Data;

@Data
public class ConfigBean implements ConfigInterface {
	private String string;
	private Long longValue;
}
