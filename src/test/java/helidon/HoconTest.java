package helidon;

import io.helidon.config.ConfigValue;
import javax.inject.Inject;
import org.jglue.cdiunit.AdditionalPackages;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 */
@RunWith(CdiRunner.class)
@AdditionalPackages(ConfigSourceFactory.class)
public class HoconTest {

    @Inject
    @ConfigurationSource("classpath:conf-config-bean.conf")
    ConfigValue<ConfigBean> hocon;

    @Test
    public void testConf() {
        ConfigBean bean = hocon.get();
        Assert.assertEquals("hocon string value", bean.getString());
        Assert.assertEquals(15L, bean.getLongValue().longValue());
    }
}
