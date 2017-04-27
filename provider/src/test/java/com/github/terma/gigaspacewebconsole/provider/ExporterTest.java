/*
Copyright 2015-2017 Artem Stasiuk

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

import com.github.terma.gigaspacewebconsole.core.ExportRequest;
import com.github.terma.gigaspacewebconsole.provider.driver.GigaSpaceUtils;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ExporterTest extends TestWithGigaSpace {

    @Before
    public void before() {
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
        request.url = gigaSpaceUrl;

        // when
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Exporter.execute(request, outputStream);

        // then
        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
        final List<String> zipFiles = new LinkedList<>();
        ZipEntry zipEntry;
        while ((zipEntry = zipInputStream.getNextEntry()) != null) zipFiles.add(zipEntry.getName());
        Assert.assertEquals("Should contains entry per driver, however: " + zipFiles, 3, zipFiles.size());
    }

    @Test
    public void shouldExportOnlyProvidedTypes() throws Exception {
        // given
        ExportRequest request = new ExportRequest();
        request.types = Arrays.asList("A");
        request.url = gigaSpaceUrl;

        // when
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Exporter.execute(request, outputStream);

        // then
        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
        final List<String> zipFiles = new LinkedList<>();
        ZipEntry zipEntry;
        while ((zipEntry = zipInputStream.getNextEntry()) != null) zipFiles.add(zipEntry.getName());
        Assert.assertEquals("Should contains entry per driver, however: " + zipFiles, 1, zipFiles.size());
    }

}
