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
import java.util.List;

import static java.util.Collections.singletonList;

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
        Assert.assertEquals(singletonList("result: class java.util.Arrays$ArrayList"), responseStream.results.get(0).columns);
        Assert.assertEquals(
                Arrays.asList(singletonList("1"), singletonList("2"), singletonList("aaa")),
                responseStream.results.get(0).data);
    }

    @Test
    public void shouldPrintArrayOfObjectAsTable() {
        ObjectGroovyExecuteResponseStream responseStream = new ObjectGroovyExecuteResponseStream();

        Object[] array = new Object[]{"a", 'v', 123};
        new PrintClosure(responseStream).call(array);

        Assert.assertEquals(1, responseStream.results.size());
        Assert.assertEquals(singletonList("result: class [Ljava.lang.Object;"), responseStream.results.get(0).columns);
        Assert.assertEquals(
                Arrays.asList(singletonList("a"), singletonList("v"), singletonList("123")),
                responseStream.results.get(0).data);
    }

    @Test
    public void shouldPrintArrayOfPrimitivesAsTable() {
        ObjectGroovyExecuteResponseStream responseStream = new ObjectGroovyExecuteResponseStream();

        int[] array = new int[]{-1, 0, 1};
        new PrintClosure(responseStream).call(array);

        Assert.assertEquals(1, responseStream.results.size());
        Assert.assertEquals(singletonList("result: class [I"), responseStream.results.get(0).columns);
        Assert.assertEquals(
                Arrays.asList(singletonList("-1"), singletonList("0"), singletonList("1")),
                responseStream.results.get(0).data);
    }

    @Test
    public void shouldPrintNull() {
        ObjectGroovyExecuteResponseStream responseStream = new ObjectGroovyExecuteResponseStream();

        new PrintClosure(responseStream).call(null);

        Assert.assertEquals(1, responseStream.results.size());
        Assert.assertEquals(singletonList("result"), responseStream.results.get(0).columns);
        Assert.assertEquals(
                singletonList(singletonList(null)),
                responseStream.results.get(0).data);
    }

}
