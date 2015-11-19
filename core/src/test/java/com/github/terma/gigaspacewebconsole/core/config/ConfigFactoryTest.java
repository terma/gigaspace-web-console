/*
Copyright 2015 Artem Stasiuk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.github.terma.gigaspacewebconsole.core.config;

import junit.framework.Assert;
import org.junit.Test;

public class ConfigFactoryTest {

    @Test
    public void shouldReadInternalConfig() {
        Assert.assertNotNull(ConfigFactory.readInternal().appVersion);
    }

    @Test
    public void shouldReadLinks() {
        System.setProperty(ConfigFactory.CONFIG_PATH_SYSTEM_PROPERTY, "classpath:/config.json");

        UserConfig userConfig = ConfigFactory.readUser();

        Assert.assertNotNull(userConfig);
        Assert.assertEquals(2, userConfig.links.size());
        Assert.assertEquals("Yandex", userConfig.links.get(0).name);
        Assert.assertEquals("http://www.yandex.ru", userConfig.links.get(0).url);
    }

    @Test
    public void shouldReadGs() {
        System.setProperty(ConfigFactory.CONFIG_PATH_SYSTEM_PROPERTY, "classpath:/config.json");

        UserConfig userConfig = ConfigFactory.readUser();

        Assert.assertNotNull(userConfig);
        Assert.assertEquals("Unexpected count of drivers", 1, userConfig.drivers.size());
        Assert.assertEquals("test", userConfig.drivers.get(0).name);
        Assert.assertEquals(2, userConfig.drivers.get(0).libs.size());
    }

    @Test
    public void shouldReadGigaspaces() {
        System.setProperty(ConfigFactory.CONFIG_PATH_SYSTEM_PROPERTY, "classpath:/config.json");

        UserConfig userConfig = ConfigFactory.readUser();

        Assert.assertNotNull(userConfig);
        Assert.assertEquals(2, userConfig.gigaspaces.size());
        Assert.assertEquals("TEST-GS-1", userConfig.gigaspaces.get(0).name);
        Assert.assertEquals("jini://*/*/testSpace?locators=locator1", userConfig.gigaspaces.get(0).url);
        Assert.assertEquals("user1", userConfig.gigaspaces.get(0).user);
        Assert.assertEquals("password1", userConfig.gigaspaces.get(0).password);
    }

    @Test
    public void shouldReadConverters() {
        System.setProperty(ConfigFactory.CONFIG_PATH_SYSTEM_PROPERTY, "classpath:/config.json");

        UserConfig userConfig = ConfigFactory.readUser();

        Assert.assertEquals(1, userConfig.converters.size());
        Assert.assertEquals("com.github.terma.gigaspacewebconsole.TestConverter", userConfig.converters.get(0));
    }

    @Test
    public void shouldReadConfigFromClasspathFile() {
        System.setProperty(ConfigFactory.CONFIG_PATH_SYSTEM_PROPERTY, "classpath:/config.json");

        UserConfig userConfig = ConfigFactory.readUser();

        Assert.assertNotNull(userConfig);
        Assert.assertEquals(2, userConfig.gigaspaces.size());
        Assert.assertEquals(1, userConfig.converters.size());
    }

    @Test
    public void shouldReadConfigFromRealFile() {
        System.setProperty(ConfigFactory.CONFIG_PATH_SYSTEM_PROPERTY, "file:src/test/resources/config.json");

        UserConfig userConfig = ConfigFactory.readUser();

        Assert.assertNotNull(userConfig);
        Assert.assertEquals(2, userConfig.gigaspaces.size());
        Assert.assertEquals(1, userConfig.converters.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfWrongConfigPathType() {
        System.setProperty(ConfigFactory.CONFIG_PATH_SYSTEM_PROPERTY, "xx:src/test/resources/config.json");
        ConfigFactory.readUser();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfSystemPropertyConfigPathNotDefined() {
        System.getProperties().remove(ConfigFactory.CONFIG_PATH_SYSTEM_PROPERTY);
        ConfigFactory.readUser();
    }

}
