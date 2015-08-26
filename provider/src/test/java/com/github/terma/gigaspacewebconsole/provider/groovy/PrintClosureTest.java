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

package com.github.terma.gigaspacewebconsole.provider.groovy;

import com.github.terma.gigaspacewebconsole.core.config.Config;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PrintClosureTest {

    @BeforeClass
    public static void init() {
        System.setProperty(Config.CONFIG_PATH_SYSTEM_PROPERTY, Config.NONE);
    }

    @Test
    public void shouldPrintListAsTable() {
        ObjectGroovyExecuteResponseStream responseStream = new ObjectGroovyExecuteResponseStream();

        List<String> list = Arrays.asList("1", "2", "aaa");
        new PrintClosure(responseStream).call(list);

        Assert.assertEquals(1, responseStream.results.size());
        Assert.assertEquals(Collections.singletonList("result"), responseStream.results.get(0).columns);
        Assert.assertEquals(
                Arrays.asList(
                        Arrays.asList("1"),
                        Arrays.asList("2"),
                        Arrays.asList("aaa")
                ),
                responseStream.results.get(0).data);
    }

}
