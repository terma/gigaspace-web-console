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

package com.github.terma.gigaspacewebconsole.provider;

import com.gigaspaces.document.DocumentProperties;
import com.gigaspaces.document.SpaceDocument;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EmbeddedObjectsConverterTest {

    @Test
    public void emptyForDocumentWithoutProperties() {
        assertThat(
                EmbeddedObjectsConverter.convert(new SpaceDocument()),
                is("{\n  \"typeName\": \"java.lang.Object\",\n  \"properties\": {}\n}"));
    }

    @Test
    public void emptyForDocumentProperties() {
        assertThat(
                EmbeddedObjectsConverter.convert(new DocumentProperties()),
                is("{}"));
    }

    @Test
    public void documentProperties() {
        DocumentProperties documentProperties = new DocumentProperties();
        documentProperties.setProperty("aaa", 123);
        documentProperties.setProperty("bb", "awer");

        assertThat(
                EmbeddedObjectsConverter.convert(documentProperties),
                is("{\n  \"aaa\": 123,\n  \"bb\": \"awer\"\n}"));
    }

    @Test
    public void ignoreNonVirtualEntry() {
        assertThat(
                EmbeddedObjectsConverter.convert(90),
                CoreMatchers.nullValue());
    }

    @Test
    public void ignoreNull() {
        assertThat(
                EmbeddedObjectsConverter.convert(null),
                CoreMatchers.nullValue());
    }

    @Test
    public void covertDocumentTypeName() {
        assertThat(
                EmbeddedObjectsConverter.convert(new SpaceDocument("MyDocument")),
                is("{\n  \"typeName\": \"MyDocument\",\n  \"properties\": {}\n}"));
    }

    @Test
    public void covertDocumentProperties() {
        SpaceDocument spaceDocument = new SpaceDocument("x");
        spaceDocument.setProperty("name", "Dark Mol");
        spaceDocument.setProperty("id", 12L);

        assertThat(
                EmbeddedObjectsConverter.convert(spaceDocument),
                is("{\n  \"typeName\": \"x\",\n  \"properties\": {\n    \"id\": 12,\n    \"name\": \"Dark Mol\"\n  }\n}"));
    }

    @Test
    public void covertDocumentStringPropertyWithQuotas() {
        SpaceDocument spaceDocument = new SpaceDocument("x");
        spaceDocument.setProperty("name", "Dark Mol");

        assertThat(
                EmbeddedObjectsConverter.convert(spaceDocument),
                is("{\n  \"typeName\": \"x\",\n  \"properties\": {\n    \"name\": \"Dark Mol\"\n  }\n}"));
    }

    @Test
    public void covertDocumentNullPropertyAsNull() {
        SpaceDocument spaceDocument = new SpaceDocument("x");
        spaceDocument.setProperty("name", null);

        assertThat(
                EmbeddedObjectsConverter.convert(spaceDocument),
                is("{\n  \"typeName\": \"x\",\n  \"properties\": {}\n}"));
    }

    @Test
    public void covertByteStream() {
        SpaceDocument spaceDocument = new SpaceDocument("x");
        spaceDocument.setProperty("name", new ByteArrayInputStream(new byte[]{1, 2}));

        assertThat(
                EmbeddedObjectsConverter.convert(spaceDocument),
                is("{\n  \"typeName\": \"x\",\n" +
                        "  \"properties\": {\n" +
                        "    \"name\": {\n" +
                        "      \"buf\": [\n" +
                        "        1,\n" +
                        "        2\n" +
                        "      ],\n" +
                        "      \"pos\": 0,\n" +
                        "      \"mark\": 0,\n" +
                        "      \"count\": 2\n" +
                        "    }\n" +
                        "  }\n}"));
    }

    @Test
    public void covertSpaceDocumentPropertyAsNested() {
        SpaceDocument nestedSpaceDocument = new SpaceDocument("nested");
        nestedSpaceDocument.setProperty("hasCar", true);
        nestedSpaceDocument.setProperty("noDrugs", false);

        SpaceDocument spaceDocument = new SpaceDocument("x");
        spaceDocument.setProperty("pp", nestedSpaceDocument);

        assertThat(
                EmbeddedObjectsConverter.convert(spaceDocument),
                is("{\n  \"typeName\": \"x\",\n" +
                        "  \"properties\": {\n" +
                        "    \"pp\": {\n" +
                        "      \"typeName\": \"nested\",\n" +
                        "      \"properties\": {\n" +
                        "        \"noDrugs\": false,\n" +
                        "        \"hasCar\": true\n" +
                        "      }\n" +
                        "    }\n" +
                        "  }\n}"));
    }

    @Test
    public void covertNestedSpaceDocumentsInCollection() {
        SpaceDocument nestedSpaceDocument1 = new SpaceDocument("i");
        nestedSpaceDocument1.setProperty("id", 1);

        SpaceDocument nestedSpaceDocument2 = new SpaceDocument("i");
        nestedSpaceDocument1.setProperty("id", 2);

        SpaceDocument spaceDocument = new SpaceDocument("x");
        spaceDocument.setProperty("list", Arrays.asList(nestedSpaceDocument1, nestedSpaceDocument2));

        assertThat(
                EmbeddedObjectsConverter.convert(spaceDocument),
                is("{\n  \"typeName\": \"x\",\n  \"properties\": {\n    \"list\": [\n      {\n        \"typeName\": \"i\",\n        \"properties\": {\n          \"id\": 2\n        }\n      },\n      {\n        \"typeName\": \"i\",\n        \"properties\": {}\n      }\n    ]\n  }\n}"));
    }

    @Test
    public void convertMap() {
        Map<String, String> map = new TreeMap<>();
        map.put("a", "12");

        assertThat(
                EmbeddedObjectsConverter.convert(map),
                is("{\n  \"a\": \"12\"\n}"));
    }

    @Test
    public void convertList() {
        List<String> list = new ArrayList<>();
        list.add("12");

        assertThat(
                EmbeddedObjectsConverter.convert(list),
                is("[\n  \"12\"\n]"));
    }

    @Test
    public void convertSet() {
        Set<String> set = new HashSet<>();
        set.add("12");

        assertThat(
                EmbeddedObjectsConverter.convert(set),
                is("[\n  \"12\"\n]"));
    }

}
