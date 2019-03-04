package helidon;

import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import io.helidon.config.ConfigValue;
import io.helidon.config.spi.AbstractParsableConfigSource.Builder;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
public class ConfigSourceFactory {
    private final ConcurrentMap<Key, ConfigValue<?>> configValues = new ConcurrentHashMap<>();

    /**
     * Create a ConfigValue from an InjectionPoint.  The injectionPoint must be annotated with a
     * {@link ConfigurationSource}.  A 'classpath:' URI value will search the classpath for matching
     * resources.
     *
     * @param injectionPoint The information about an injected member
     * @return The ConfigurationCache
     */
    @Produces
    @ConfigurationSource("")
    <T> ConfigValue<T> getConfigValue(InjectionPoint injectionPoint) {
        Key key = new Key(injectionPoint);
        @SuppressWarnings("unchecked") ConfigValue<T> configValue = (ConfigValue<T>) configValues.get(key);
        if (configValue == null) {
            synchronized (configValues) {
                configValue = key.createConfigValue();
                configValues.put(key, configValue);
            }
        }
        return configValue;
    }

    @Getter
    @EqualsAndHashCode
    private static class Key<T> {
        private final URI source;
        private final Class<T> type;

        Key(InjectionPoint injectionPoint) {
            source = getUriFromInjectionPoint(injectionPoint);
            type = (Class) ((ParameterizedType) injectionPoint.getType()).getActualTypeArguments()[0];
        }

        ConfigValue<T> createConfigValue() {
            return Config.builder(createBuilder())
                    .disableEnvironmentVariablesSource()
                    .disableSystemPropertiesSource()
                    .build()
                .as(type);
        }

        @SneakyThrows
        Builder createBuilder() {
            switch (source.getScheme()) {
            case "file":
                return ConfigSources.file(source.getSchemeSpecificPart());
            case "classpath":
                return ConfigSources.classpath(source.getSchemeSpecificPart());
            }
            return ConfigSources.url(source.toURL());
        }
    }

    private static String packagePathOfInjectionSite(InjectionPoint injectionPoint) {
        return injectionPoint.getMember().getDeclaringClass()
                .getPackage().getName().replace('.', '/');
    }

    @SneakyThrows
    private static URI getUriFromInjectionPoint(InjectionPoint injectionPoint) {
        ConfigurationSource cs = injectionPoint.getAnnotated().getAnnotation(ConfigurationSource.class);
        URI uri = new URI(cs.value());
        if (uri.getScheme().equals("classpath")) {
            // cannonicalize 'classpath' resources to include the package name
            String ssp = uri.getSchemeSpecificPart();
            if (ssp.charAt(0) != '/') {
                return new URI("classpath:" + packagePathOfInjectionSite(injectionPoint) + '/' + ssp);
            }
        }
        return uri;
    }
}
