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

package com.github.terma.gigaspacesqlconsole.provider;

import com.gigaspaces.document.SpaceDocument;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class VirtualEntryConverterTest {

    @Test
    public void nullForNull() {
        assertThat(VirtualEntryConverter.convert(null), is("null"));
    }

    @Test
    public void emptyForDocumentWithoutProperties() {
        assertThat(
                VirtualEntryConverter.convert(new SpaceDocument()),
                is("{\n  \"typeName\": \"java.lang.Object\",\n  \"properties\": {}\n}"));
    }

    @Test
    public void showDocumentTypeName() {
        assertThat(
                VirtualEntryConverter.convert(new SpaceDocument("MyDocument")),
                is("{\n  \"typeName\": \"MyDocument\",\n  \"properties\": {}\n}"));
    }

    @Test
    public void showDocumentProperties() {
        SpaceDocument spaceDocument = new SpaceDocument("x");
        spaceDocument.setProperty("name", "Dark Mol");
        spaceDocument.setProperty("id", 12L);

        assertThat(
                VirtualEntryConverter.convert(spaceDocument),
                is("{\n  \"typeName\": \"x\",\n  \"properties\": {\n    \"id\": 12,\n    \"name\": \"Dark Mol\"\n  }\n}"));
    }

    @Test
    public void showDocumentStringPropertyWithQuotas() {
        SpaceDocument spaceDocument = new SpaceDocument("x");
        spaceDocument.setProperty("name", "Dark Mol");

        assertThat(
                VirtualEntryConverter.convert(spaceDocument),
                is("{\n  \"typeName\": \"x\",\n  \"properties\": {\n    \"name\": \"Dark Mol\"\n  }\n}"));
    }

    @Test
    public void showDocumentNullPropertyAsNull() {
        SpaceDocument spaceDocument = new SpaceDocument("x");
        spaceDocument.setProperty("name", null);

        assertThat(
                VirtualEntryConverter.convert(spaceDocument),
                is("{\n  \"typeName\": \"x\",\n  \"properties\": {}\n}"));
    }

    @Test
    public void showByteStream() {
        SpaceDocument spaceDocument = new SpaceDocument("x");
        spaceDocument.setProperty("name", new ByteArrayInputStream(new byte[] {1,2}));

        assertThat(
                VirtualEntryConverter.convert(spaceDocument),
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
    public void showSpaceDocumentPropertyAsNested() {
        SpaceDocument nestedSpaceDocument = new SpaceDocument("nested");
        nestedSpaceDocument.setProperty("hasCar", true);
        nestedSpaceDocument.setProperty("noDrugs", false);

        SpaceDocument spaceDocument = new SpaceDocument("x");
        spaceDocument.setProperty("pp", nestedSpaceDocument);

        assertThat(
                VirtualEntryConverter.convert(spaceDocument),
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
    public void showNestedSpaceDocumentsInCollection() {
        SpaceDocument nestedSpaceDocument1 = new SpaceDocument("i");
        nestedSpaceDocument1.setProperty("id", 1);

        SpaceDocument nestedSpaceDocument2 = new SpaceDocument("i");
        nestedSpaceDocument1.setProperty("id", 2);

        SpaceDocument spaceDocument = new SpaceDocument("x");
        spaceDocument.setProperty("list", Arrays.asList(nestedSpaceDocument1, nestedSpaceDocument2));

        assertThat(
                VirtualEntryConverter.convert(spaceDocument),
                is("{\n  \"typeName\": \"x\",\n  \"properties\": {\n    \"list\": [\n      {\n        \"typeName\": \"i\",\n        \"properties\": {\n          \"id\": 2\n        }\n      },\n      {\n        \"typeName\": \"i\",\n        \"properties\": {}\n      }\n    ]\n  }\n}"));
    }

}
