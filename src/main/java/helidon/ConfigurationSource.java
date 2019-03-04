package helidon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * Annotation to indicate the configuration source for {@link io.helidon.config.ConfigValue} injections.
 * The value of this annotation is a URI which locates the configuration file.  The URI schemes
 * of 'file', 'classpath', 'http', and 'https' are supported.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
public @interface ConfigurationSource {
	static final long FIVE_MINUTES = 5 * 60 * 60 * 1000;

	/**
	 * The source of the configuration.  Usually in the form of a URL such as file:///etc/config/integration.json,
	 * classpath:application.yaml
	 * @return The source of the configuration.
	 */
	@Nonbinding String value();

	/**
	 * The polling interval.  This allows refresh of the configuration.
	 * @return The number of milliseconds between polling for newer configuration.
	 */	
	@Nonbinding long interval() default FIVE_MINUTES;
}
