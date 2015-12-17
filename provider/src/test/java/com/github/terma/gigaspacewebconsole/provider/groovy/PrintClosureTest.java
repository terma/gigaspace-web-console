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

import com.gigaspaces.document.SpaceDocument;
import com.github.terma.gigaspacewebconsole.core.config.ConfigFactory;
import com.github.terma.gigaspacewebconsole.core.ArrayIterable;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

import static java.util.Collections.singletonList;

public class PrintClosureTest {

    @BeforeClass
    public static void init() {
        System.setProperty(ConfigFactory.CONFIG_PATH_SYSTEM_PROPERTY, ConfigFactory.NONE);
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

    @Test
    public void shouldPrintMapAsTableWithTwoColumns() {
        ObjectGroovyExecuteResponseStream responseStream = new ObjectGroovyExecuteResponseStream();

        Map<Object, Object> map = new TreeMap<>();
        map.put("a", 1);
        map.put("b", 12);
        new PrintClosure(responseStream).call(map);

        Assert.assertEquals(1, responseStream.results.size());
        Assert.assertEquals(
                Arrays.asList("key", "value"),
                responseStream.results.get(0).columns);
        Assert.assertEquals(
                Arrays.asList(Arrays.asList("a", "1"), Arrays.asList("b", "12")),
                responseStream.results.get(0).data);
    }

    @Test
    public void shouldPrintIterableOfSpaceDocumentsAsTable() {
        ObjectGroovyExecuteResponseStream responseStream = new ObjectGroovyExecuteResponseStream();

        SpaceDocument spaceDocument1 = new SpaceDocument();
        spaceDocument1.setProperty("id", 1);
        Iterable iterable = new ArrayIterable(new Object[]{spaceDocument1});

        new PrintClosure(responseStream).call(iterable);

        Assert.assertEquals(1, responseStream.results.size());
        Assert.assertEquals(
                singletonList("id"),
                responseStream.results.get(0).columns);
        Assert.assertEquals(
                singletonList(singletonList("1")),
                responseStream.results.get(0).data);
    }

    @Test
    public void shouldPrintArrayOfSpaceDocumentsAsTable() {
        ObjectGroovyExecuteResponseStream responseStream = new ObjectGroovyExecuteResponseStream();

        SpaceDocument spaceDocument = new SpaceDocument();
        spaceDocument.setProperty("id", 1);
        SpaceDocument[] spaceDocuments = new SpaceDocument[]{spaceDocument};

        new PrintClosure(responseStream).call(spaceDocuments);

        Assert.assertEquals(1, responseStream.results.size());
        Assert.assertEquals(
                singletonList("id"),
                responseStream.results.get(0).columns);
        Assert.assertEquals(
                singletonList(singletonList("1")),
                responseStream.results.get(0).data);
    }

    @Test
    public void shouldPrintAllColumnsForSpaceDocumentsAsTable() {
        ObjectGroovyExecuteResponseStream responseStream = new ObjectGroovyExecuteResponseStream();

        SpaceDocument spaceDocument1 = new SpaceDocument();
        spaceDocument1.setProperty("id", 1);
        SpaceDocument spaceDocument2 = new SpaceDocument();
        spaceDocument2.setProperty("id", 12);
        spaceDocument2.setProperty("text", "next");
        SpaceDocument[] spaceDocuments = new SpaceDocument[]{spaceDocument1, spaceDocument2};

        new PrintClosure(responseStream).call(spaceDocuments);

        Assert.assertEquals(1, responseStream.results.size());
        Assert.assertEquals(
                Arrays.asList("id", "text"),
                responseStream.results.get(0).columns);
        Assert.assertEquals(
                Arrays.asList(Arrays.asList("1", null), Arrays.asList("12", "next")),
                responseStream.results.get(0).data);
    }

    @Test
    public void shouldPrintSpaceDocumentsWithNull() {
        ObjectGroovyExecuteResponseStream responseStream = new ObjectGroovyExecuteResponseStream();

        SpaceDocument spaceDocument1 = new SpaceDocument();
        spaceDocument1.setProperty("id", 1);
        SpaceDocument[] spaceDocuments = new SpaceDocument[]{spaceDocument1, null};

        new PrintClosure(responseStream).call(spaceDocuments);

        Assert.assertEquals(1, responseStream.results.size());
        Assert.assertEquals(
                singletonList("id"),
                responseStream.results.get(0).columns);
        Assert.assertEquals(
                Arrays.asList(singletonList("1"), singletonList(null)),
                responseStream.results.get(0).data);
    }

    @Test
    public void shouldPrintCollectionsOfNulls() {
        ObjectGroovyExecuteResponseStream responseStream = new ObjectGroovyExecuteResponseStream();

        SpaceDocument[] spaceDocuments = new SpaceDocument[]{null, null};

        new PrintClosure(responseStream).call(spaceDocuments);

        Assert.assertEquals(1, responseStream.results.size());
        Assert.assertEquals(
                Collections.emptyList(),
                responseStream.results.get(0).columns);
        Assert.assertEquals(
                Arrays.asList(Collections.emptyList(), Collections.emptyList()),
                responseStream.results.get(0).data);
    }

    @Test
    public void shouldPrintMixedIterableWithSpaceDocumentsAndOtherTypesAsTable() {
        ObjectGroovyExecuteResponseStream responseStream = new ObjectGroovyExecuteResponseStream();

        SpaceDocument spaceDocument = new SpaceDocument();
        spaceDocument.setProperty("id", 0);
        spaceDocument.setProperty("name", "A");
        Object[] array = new Object[]{spaceDocument, "123"};

        new PrintClosure(responseStream).call(array);

        Assert.assertEquals(1, responseStream.results.size());
        Assert.assertEquals(
                singletonList("result: class [Ljava.lang.Object;"),
                responseStream.results.get(0).columns);
        Assert.assertEquals(
                Arrays.asList(singletonList("{\n" +
                        "  \"typeName\": \"java.lang.Object\",\n" +
                        "  \"properties\": {\n" +
                        "    \"id\": 0,\n" +
                        "    \"name\": \"A\"\n" +
                        "  }\n" +
                        "}"
                ), singletonList("123")),
                responseStream.results.get(0).data);
    }

    @Test
    public void shouldPrintSingleSpaceDocumentAsOneRowTable() {
        ObjectGroovyExecuteResponseStream responseStream = new ObjectGroovyExecuteResponseStream();

        SpaceDocument spaceDocument = new SpaceDocument();
        spaceDocument.setProperty("id", 0);
        spaceDocument.setProperty("name", "A");

        new PrintClosure(responseStream).call(spaceDocument);

        Assert.assertEquals(1, responseStream.results.size());
        Assert.assertEquals(
                Arrays.asList("id", "name"),
                responseStream.results.get(0).columns);
        Assert.assertEquals(
                singletonList(Arrays.asList("0", "A")),
                responseStream.results.get(0).data);
    }

}
