package com.github.terma.gigaspacesqlconsole.config;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class ConfigTest {

    @Test
    public void shouldReadLinks() {
        System.setProperty(Config.CONFIG_PATH_SYSTEM_PROPERTY, "classpath:/config.json");

        Config config = Config.read();

        assertNotNull(config);
        assertEquals(2, config.links.size());
        assertEquals("Yandex", config.links.get(0).name);
        assertEquals("http://www.yandex.ru", config.links.get(0).url);
    }

    @Test
    public void shouldReadGigaspaces() {
        System.setProperty(Config.CONFIG_PATH_SYSTEM_PROPERTY, "classpath:/config.json");

        Config config = Config.read();

        assertNotNull(config);
        assertEquals(2, config.gigaspaces.size());
        assertEquals("TEST-GS-1", config.gigaspaces.get(0).name);
        assertEquals("jini://*/*/testSpace?locators=locator1", config.gigaspaces.get(0).url);
        assertEquals("user1", config.gigaspaces.get(0).user);
        assertEquals("password1", config.gigaspaces.get(0).password);
    }

    @Test
    public void shouldReadConverters() {
        System.setProperty(Config.CONFIG_PATH_SYSTEM_PROPERTY, "classpath:/config.json");

        Config config = Config.read();

        assertEquals(1, config.converters.size());
        assertEquals("com.github.terma.gigaspacesqlconsole.TestConverter", config.converters.get(0));
    }

    @Test
    public void shouldReadConfigFromClasspathFile() {
        System.setProperty(Config.CONFIG_PATH_SYSTEM_PROPERTY, "classpath:/config.json");

        Config config = Config.read();

        assertNotNull(config);
        assertEquals(2, config.gigaspaces.size());
        assertEquals(1, config.converters.size());
    }

    @Test
    public void shouldReadConfigFromRealFile() {
        System.setProperty(Config.CONFIG_PATH_SYSTEM_PROPERTY, "file:src/test/resources/config.json");

        Config config = Config.read();

        assertNotNull(config);
        assertEquals(2, config.gigaspaces.size());
        assertEquals(1, config.converters.size());
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
