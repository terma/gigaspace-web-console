package com.github.terma.gigaspacesqlconsole.config;

import junit.framework.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class ConfigTest {

    @Test
    public void shouldReadInternalConfig() {
        Assert.assertNotNull(Config.readInternal().applicationVersion);
    }

    @Test
    public void shouldReadLinks() {
        System.setProperty(Config.CONFIG_PATH_SYSTEM_PROPERTY, "classpath:/config.json");

        UserConfig userConfig = Config.readUser();

        assertNotNull(userConfig);
        assertEquals(2, userConfig.links.size());
        assertEquals("Yandex", userConfig.links.get(0).name);
        assertEquals("http://www.yandex.ru", userConfig.links.get(0).url);
    }

    @Test
    public void shouldReadGigaspaces() {
        System.setProperty(Config.CONFIG_PATH_SYSTEM_PROPERTY, "classpath:/config.json");

        UserConfig userConfig = Config.readUser();

        assertNotNull(userConfig);
        assertEquals(2, userConfig.gigaspaces.size());
        assertEquals("TEST-GS-1", userConfig.gigaspaces.get(0).name);
        assertEquals("jini://*/*/testSpace?locators=locator1", userConfig.gigaspaces.get(0).url);
        assertEquals("user1", userConfig.gigaspaces.get(0).user);
        assertEquals("password1", userConfig.gigaspaces.get(0).password);
    }

    @Test
    public void shouldReadConverters() {
        System.setProperty(Config.CONFIG_PATH_SYSTEM_PROPERTY, "classpath:/config.json");

        UserConfig userConfig = Config.readUser();

        assertEquals(1, userConfig.converters.size());
        assertEquals("com.github.terma.gigaspacesqlconsole.TestConverter", userConfig.converters.get(0));
    }

    @Test
    public void shouldReadConfigFromClasspathFile() {
        System.setProperty(Config.CONFIG_PATH_SYSTEM_PROPERTY, "classpath:/config.json");

        UserConfig userConfig = Config.readUser();

        assertNotNull(userConfig);
        assertEquals(2, userConfig.gigaspaces.size());
        assertEquals(1, userConfig.converters.size());
    }

    @Test
    public void shouldReadConfigFromRealFile() {
        System.setProperty(Config.CONFIG_PATH_SYSTEM_PROPERTY, "file:src/test/resources/config.json");

        UserConfig userConfig = Config.readUser();

        assertNotNull(userConfig);
        assertEquals(2, userConfig.gigaspaces.size());
        assertEquals(1, userConfig.converters.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfWrongConfigPathType() {
        System.setProperty(Config.CONFIG_PATH_SYSTEM_PROPERTY, "xx:src/test/resources/config.json");
        Config.readUser();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfSystemPropertyConfigPathNotDefined() {
        System.getProperties().remove(Config.CONFIG_PATH_SYSTEM_PROPERTY);
        Config.readUser();
    }

}
