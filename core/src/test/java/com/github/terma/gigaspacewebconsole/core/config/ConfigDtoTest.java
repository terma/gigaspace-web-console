/*
Copyright 2015-2016 Artem Stasiuk
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

public class ConfigDtoTest {

    @Test
    public void justCodeCoverageForDtoClasses() {
        Assert.assertEquals("ConfigDatabase {name:'null', url:'null', user:'null', driver:'null', secure:false, unmanaged: false}", new ConfigDatabase().toString());
        Assert.assertEquals("ConfigDriver {name: 'null', libs: null}", new ConfigDriver().toString());
        Assert.assertEquals("ConfigLink {name: 'null', url: 'null'}", new ConfigLink().toString());
        Assert.assertEquals("ConfigTemplate {name: 'null', sql: 'null'}", new ConfigTemplate().toString());
        Assert.assertEquals("InternalConfig {appVersion: 'null'}", new InternalConfig().toString());
        Assert.assertEquals("Config {user: null, internal: null}", new Config().toString());

        Assert.assertEquals(
                "UserConfig {version: 'null', links: [], converters: [], gigaspaces: [], drivers: [], templates: []}",
                new UserConfig().toString());

        Assert.assertEquals(
                "Config {user: null, internal: null}",
                new Config().toString());
    }

}
