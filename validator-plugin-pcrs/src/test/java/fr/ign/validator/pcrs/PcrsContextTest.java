package fr.ign.validator.pcrs;

import org.junit.Assert;
import org.junit.Test;

import fr.ign.validator.Context;
import fr.ign.validator.plugin.PluginManager;

public class PcrsContextTest {

    /**
     * Test behavior for default constructor
     */
    @Test
    public void testDefaultConstructor() {
        Context context = new Context();

        Assert.assertEquals("UTF-8", context.getEncoding().name());
        Assert.assertFalse(context.isFlatValidation());
    }

    @Test
    public void testPcrsDefaultConstructor() {
        Context context = new Context();
        PluginManager pluginManager = new PluginManager();
        pluginManager.getPluginByName(PcrsPlugin.NAME).setup(context);

        Assert.assertEquals("UTF-8", context.getEncoding().name());
        Assert.assertTrue(context.isFlatValidation());
    }

}
