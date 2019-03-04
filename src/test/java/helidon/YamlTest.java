package helidon;

import io.helidon.config.ConfigValue;
import javax.inject.Inject;
import org.jglue.cdiunit.AdditionalPackages;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CdiRunner.class)
@AdditionalPackages(ConfigSourceFactory.class)
public class YamlTest {
	
	@Inject
	@ConfigurationSource("classpath:yaml-config-bean.yaml")
	ConfigValue<ConfigBean> yml;
	
	@Test
	public void testYaml() {
		ConfigBean bean = yml.get();
		Assert.assertEquals("yaml string value", bean.getString());
		Assert.assertEquals(15L, bean.getLongValue().longValue());
	}
}
