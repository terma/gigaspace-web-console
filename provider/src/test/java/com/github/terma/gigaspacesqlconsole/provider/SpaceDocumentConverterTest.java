package com.github.terma.gigaspacesqlconsole.provider;

import com.gigaspaces.document.SpaceDocument;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class SpaceDocumentConverterTest {

    private final SpaceDocumentConverter converter = new SpaceDocumentConverter();

    @Test
    public void nullForNull() {
        assertThat(converter.convert(null), nullValue());
    }

    @Test
    public void emptyForDocumentWithoutProperties() {
        assertThat(
                converter.convert(new SpaceDocument()),
                is("java.lang.Object (com.gigaspaces.document.SpaceDocument) {}"));
    }

    @Test
    public void showDocumentTypeName() {
        assertThat(
                converter.convert(new SpaceDocument("MyDocument")),
                is("MyDocument (com.gigaspaces.document.SpaceDocument) {}"));
    }

    @Test
    public void showDocumentClassName() {
        assertThat(
                converter.convert(new SpaceDocument("x")),
                is("x (com.gigaspaces.document.SpaceDocument) {}"));
    }

    @Test
    public void showDocumentProperties() {
        SpaceDocument spaceDocument = new SpaceDocument("x");
        spaceDocument.setProperty("name", "Dark Mol");
        spaceDocument.setProperty("id", 12L);

        assertThat(
                converter.convert(spaceDocument),
                is("x (com.gigaspaces.document.SpaceDocument) {\n  id: 12,\n  name: \"Dark Mol\"\n}"));
    }

    @Test
    public void showDocumentStringPropertyWithQuotas() {
        SpaceDocument spaceDocument = new SpaceDocument("x");
        spaceDocument.setProperty("name", "Dark Mol");

        assertThat(
                converter.convert(spaceDocument),
                is("x (com.gigaspaces.document.SpaceDocument) {\n  name: \"Dark Mol\"\n}"));
    }

    @Test
    public void showDocumentNullPropertyAsNull() {
        SpaceDocument spaceDocument = new SpaceDocument("x");
        spaceDocument.setProperty("name", null);

        assertThat(
                converter.convert(spaceDocument),
                is("x (com.gigaspaces.document.SpaceDocument) {\n  name: null\n}"));
    }

    @Test
    public void showSpaceDocumentPropertyAsNested() {
        SpaceDocument nestedSpaceDocument = new SpaceDocument("nested");
        nestedSpaceDocument.setProperty("hasCar", true);
        nestedSpaceDocument.setProperty("noDrugs", false);

        SpaceDocument spaceDocument = new SpaceDocument("x");
        spaceDocument.setProperty("pp", nestedSpaceDocument);

        assertThat(
                converter.convert(spaceDocument),
                is("x (com.gigaspaces.document.SpaceDocument) {\n" +
                        "  pp: nested (com.gigaspaces.document.SpaceDocument) {\n" +
                        "    noDrugs: false,\n" +
                        "    hasCar: true\n" +
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
                converter.convert(spaceDocument),
                is("x (com.gigaspaces.document.SpaceDocument) {\n" +
                        "  list: [i (com.gigaspaces.document.SpaceDocument) {\n" +
                        "    id: 1,\n" +
                        "  }\n}"));
    }

}
