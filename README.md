# Use [CDI](http://www.cdi-spec.org/) to inject application configuration.

ConfigurationCache is a generic class that reads configuration documents and fills pojos (**P**lain **O**ld **J**ava **O**bjects) or reifies pojis (**P**lain **O**ld **J**ava **I**nterfaces) with the parsed values.  With this library CDI will inject ConfigurationCache sites marked with @Inject and @ConfigurationSource.  The configuration source is read from the location specified in the @ConfigurationSource annotation.

```java
public class MyClassThatNeedsConfiguration {

	@Inject @ConfigurationSource("classpath:conf-config-bean.conf")
	private ConfigurationCache<ConfigBean> configCache;
    
    public String doSomethingThatNeedsConfiguration() {
    	ConfigBean config = configCache.get();
        // use values from config
        // ...
	}
}
```

## Features
 * Thread safe
 * Multiple configuration source document formats
 * Automatic reload of configuration values when document changes
 * Configuration set consistency
 * Annotation driven document source
 * Annotation driven document to object mapping
 * Configuration Interfaces as well as Objects

## @ConfigurationSource elements
ConfigurationSource has two elements:
 * value - URI to locate the configuration source document.  The file extension is used to determine the document data format.
 * interval - Number of milliseconds before refetching source document

## Supported Data Formats
Configuration-cdi supports the following source file formats:
 * [Json](http://www.json.org/)
 * [Conf](https://github.com/typesafehub/config/blob/master/HOCON.md)
 * [Xml](https://www.w3.org/TR/REC-xml/)
 * [Yaml](http://www.yaml.org/spec/)

### Single document to multiple configuration objects
It is possible to have a single document specified by multiple ConfigurationCache injection points.  Each resultant object will be filled with its subset of attributes defined in the document.

### Document per configuration object
It is also possible to have a document specified for each ConfigurationCache injection point. 

### Extra attributes
Attributes specified in the document without a corresponding configuration object attribute are ignored.

### Missing attributes
Configuration object attributes without a corresponding attribute in the document are left as initialized by java.


## Add to your maven project
```xml
<dependency>
  <groupId>org.honton.chas</groupId>
  <artifactId>config-cdi</artifactId>
  <version>0.1.2</version>
</dependency>
```

### Add optional support for configuration interfaces 
```xml
<dependency>
  <groupId>com.fasterxml.jackson.module</groupId>
  <artifactId>jackson-module-mrbean</artifactId>
  <version>2.7.4</version>
</dependency>
```

### Add optional support for Yaml 
```xml
<dependency>
  <groupId>com.fasterxml.jackson.dataformat</groupId>
  <artifactId>jackson-dataformat-yaml</artifactId>
  <version>2.9.8</version>
</dependency>
```

### Add optional support for Conf 
```xml
<dependency>
  <groupId>com.jasonclawson</groupId>
  <artifactId>jackson-dataformat-hocon</artifactId>
  <version>1.1.1</version>
</dependency>
```

### Add optional support for Xml 
```xml
<dependency>
  <groupId>com.fasterxml.jackson.dataformat</groupId>
  <artifactId>jackson-dataformat-xml</artifactId>
  <version>2.9.8</version>
</dependency>
```

### Mapping Annotations 
[Jackson](https://github.com/FasterXML/jackson) is used to parse documents and populate the configuration pojos.  All of the [standard Jackson annotations](https://github.com/FasterXML/jackson-annotations/wiki/Jackson-Annotations) can be used to direct the document to object mapping.
