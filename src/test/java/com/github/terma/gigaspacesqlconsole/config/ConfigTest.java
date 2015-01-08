package com.github.terma.gigaspacesqlconsole.config;

import junit.framework.Assert;
import org.junit.Test;

public class ConfigTest {

    @Test
    public void shouldReadGigaspaces() {
        System.setProperty(Config.CONFIG_PATH_SYSTEM_PROPERTY, "classpath:/config.json");

        Config config = Config.read();

        Assert.assertNotNull(config);
        Assert.assertEquals(2, config.gigaspaces.size());
        Assert.assertEquals("TEST-GS-1", config.gigaspaces.get(0).name);
        Assert.assertEquals("jini://*/*/testSpace?locators=locator1", config.gigaspaces.get(0).url);
        Assert.assertEquals("user1", config.gigaspaces.get(0).user);
        Assert.assertEquals("password1", config.gigaspaces.get(0).password);
    }

    @Test
    public void shouldReadConverters() {
        System.setProperty(Config.CONFIG_PATH_SYSTEM_PROPERTY, "classpath:/config.json");

        Config config = Config.read();

        Assert.assertEquals(1, config.converters.size());
        Assert.assertEquals("com.github.terma.gigaspacesqlconsole.TestConverter", config.converters.get(0));
    }

    @Test
    public void shouldReadConfigFromClasspathFile() {
        System.setProperty(Config.CONFIG_PATH_SYSTEM_PROPERTY, "classpath:/config.json");

        Config config = Config.read();

        Assert.assertNotNull(config);
        Assert.assertEquals(2, config.gigaspaces.size());
        Assert.assertEquals(1, config.converters.size());
    }

    @Test
    public void shouldReadConfigFromRealFile() {
        System.setProperty(Config.CONFIG_PATH_SYSTEM_PROPERTY, "file:src/test/resources/config.json");

        Config config = Config.read();

        Assert.assertNotNull(config);
        Assert.assertEquals(2, config.gigaspaces.size());
        Assert.assertEquals(1, config.converters.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfWrongConfigPathType() {
        System.setProperty(Config.CONFIG_PATH_SYSTEM_PROPERTY, "xx:src/test/resources/config.json");
        Config.read();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfSystemPropertyConfigPathNotDefined() {
        System.getProperties().remove(Config.CONFIG_PATH_SYSTEM_PROPERTY);
        Config.read();
    }

}
