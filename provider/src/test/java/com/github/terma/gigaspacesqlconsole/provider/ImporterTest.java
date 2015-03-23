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
    private GigaSpace gigaSpace;
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
        GigaSpaceUtils.registerType(exportGigaSpace, "C");

        ExportRequest request = new ExportRequest();
        request.url = "/./export-import";

        // when
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Exporter.execute(request, outputStream);
        zip = outputStream.toByteArray();

        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zip));
        zipInputStream.getNextEntry(); // Object
        zipInputStream.getNextEntry(); // A
        final ByteArrayOutputStream aSerOutputStream = new ByteArrayOutputStream();
        int data;
        while ((data = zipInputStream.read()) > -1) aSerOutputStream.write(data);
        aSer = aSerOutputStream.toByteArray();

        gigaSpace = GigaSpaceUtils.getGigaSpace("/./import");
        GigaSpaceUtils.registerType(gigaSpace, "A");
        gigaSpace.clear(null);
    }

    @Test
    public void shouldImportFromSerFile() throws Exception {
        // given
        ImportRequest request = new ImportRequest();
        request.file = "my.ser";
        request.url = "/./import";

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
