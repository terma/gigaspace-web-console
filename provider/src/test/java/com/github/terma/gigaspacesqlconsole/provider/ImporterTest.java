package com.github.terma.gigaspacesqlconsole.provider;

import com.gigaspaces.document.SpaceDocument;
import com.github.terma.gigaspacesqlconsole.core.ExportRequest;
import com.github.terma.gigaspacesqlconsole.core.ImportRequest;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openspaces.core.GigaSpace;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.zip.ZipInputStream;

public class ImporterTest {

    private GigaSpace exportGigaSpace;
    private byte[] zip;
    private byte[] aSer;

    @Before
    public void before() throws Exception {
        exportGigaSpace = GigaSpaceUtils.getGigaSpace("/./export-import");
        exportGigaSpace.clear(null);

        GigaSpaceUtils.registerType(exportGigaSpace, "A");
        GigaSpaceUtils.writeDocument(exportGigaSpace, "A");
        GigaSpaceUtils.writeDocument(exportGigaSpace, "A");
        Assert.assertEquals(2, exportGigaSpace.count(new SpaceDocument("A")));

        GigaSpaceUtils.registerType(exportGigaSpace, "B");
        GigaSpaceUtils.writeDocument(exportGigaSpace, "B");
        Assert.assertEquals(1, exportGigaSpace.count(new SpaceDocument("B")));

        GigaSpaceUtils.registerType(exportGigaSpace, "C");

        ExportRequest request = new ExportRequest();
        request.url = "/./export-import";

        // when
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Exporter.execute(request, outputStream);
        zip = outputStream.toByteArray();

        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zip));
        zipInputStream.getNextEntry(); // A
        final ByteArrayOutputStream aSerOutputStream = new ByteArrayOutputStream();
        int data;
        while ((data = zipInputStream.read()) > -1) aSerOutputStream.write(data);
        aSer = aSerOutputStream.toByteArray();
    }

    @Test
    public void shouldImportFromSerFile() throws Exception {
        final GigaSpace gigaSpace = GigaSpaceUtils.getGigaSpace("/./import1");
        GigaSpaceUtils.registerType(gigaSpace, "A");

        // given
        ImportRequest request = new ImportRequest();
        request.file = "my.ser";
        request.url = "/./import1";

        // when
        ByteArrayInputStream inputStream = new ByteArrayInputStream(aSer);
        Importer.execute(request, inputStream);

        // then
        SpaceDocument template = new SpaceDocument("A");
        Assert.assertEquals(
                new HashSet<>(Arrays.asList(exportGigaSpace.readMultiple(template))),
                new HashSet<>(Arrays.asList(gigaSpace.readMultiple(template))));
    }

    @Test
    public void shouldImportFromZipWithSerFiles() throws Exception {
        final GigaSpace gigaSpace = GigaSpaceUtils.getGigaSpace("/./import-from-zip");
        GigaSpaceUtils.registerType(gigaSpace, "A");

        // given
        ImportRequest request = new ImportRequest();
        request.file = "my.zip";
        request.url = "/./import-from-zip";

        // when
        ByteArrayInputStream inputStream = new ByteArrayInputStream(zip);
        Importer.execute(request, inputStream);

        // then
        SpaceDocument template = new SpaceDocument("A");
        Assert.assertEquals(
                new HashSet<>(Arrays.asList(exportGigaSpace.readMultiple(template))),
                new HashSet<>(Arrays.asList(gigaSpace.readMultiple(template))));

        SpaceDocument templateB = new SpaceDocument("B");
        Assert.assertEquals(
                new HashSet<>(Arrays.asList(exportGigaSpace.readMultiple(templateB))),
                new HashSet<>(Arrays.asList(gigaSpace.readMultiple(templateB))));
    }

    @Test
    public void shouldRegisterExportedTypesDuringImport() throws Exception {
        // given
        final GigaSpace gigaSpace = GigaSpaceUtils.getGigaSpace("/./import-no-types");
        ImportRequest request = new ImportRequest();
        request.file = "my.ser";
        request.url = "/./import-no-types";

        // when
        ByteArrayInputStream inputStream = new ByteArrayInputStream(aSer);
        Importer.execute(request, inputStream);

        // then
        SpaceDocument template = new SpaceDocument("A");
        Assert.assertEquals(
                new HashSet<>(Arrays.asList(exportGigaSpace.readMultiple(template))),
                new HashSet<>(Arrays.asList(gigaSpace.readMultiple(template))));
    }

}
