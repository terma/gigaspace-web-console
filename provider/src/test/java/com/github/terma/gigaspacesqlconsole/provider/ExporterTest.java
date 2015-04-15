package com.github.terma.gigaspacesqlconsole.provider;

import com.github.terma.gigaspacesqlconsole.core.ExportRequest;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openspaces.core.GigaSpace;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ExporterTest {

    private GigaSpace gigaSpace;

    @Before
    public void before() {
        gigaSpace = GigaSpaceUtils.getGigaSpace("/./export");
        gigaSpace.clear(null);

        GigaSpaceUtils.registerType(gigaSpace, "A");
        GigaSpaceUtils.writeDocument(gigaSpace, "A");
        GigaSpaceUtils.writeDocument(gigaSpace, "A");

        GigaSpaceUtils.registerType(gigaSpace, "B");
        GigaSpaceUtils.registerType(gigaSpace, "C");
    }

    @Test
    public void shouldExportAllTypesToFile() throws Exception {
        // given
        ExportRequest request = new ExportRequest();
        request.url = "/./export";

        // when
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Exporter.execute(request, outputStream);

        // then
        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
        final List<String> zipFiles = new LinkedList<>();
        ZipEntry zipEntry;
        while ((zipEntry = zipInputStream.getNextEntry()) != null) zipFiles.add(zipEntry.getName());
        Assert.assertEquals("Should contains entry per type, however: " + zipFiles, 3, zipFiles.size());
    }

    @Test
    public void shouldExportOnlyProvidedTypes() throws Exception {
        // given
        ExportRequest request = new ExportRequest();
        request.types = Arrays.asList("A");
        request.url = "/./export";

        // when
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Exporter.execute(request, outputStream);

        // then
        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
        final List<String> zipFiles = new LinkedList<>();
        ZipEntry zipEntry;
        while ((zipEntry = zipInputStream.getNextEntry()) != null) zipFiles.add(zipEntry.getName());
        Assert.assertEquals("Should contains entry per type, however: " + zipFiles, 1, zipFiles.size());
    }

}
