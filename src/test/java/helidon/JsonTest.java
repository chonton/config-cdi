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
public class JsonTest {
	
	@Inject
	@ConfigurationSource("classpath:json-config-bean.json")
	ConfigValue<ConfigBean> json;
	
	@Test
	public void testJson() {
		ConfigBean bean = json.get();
		Assert.assertEquals("json string value", bean.getString());
		Assert.assertEquals(15L, bean.getLongValue().longValue());
	}
}
