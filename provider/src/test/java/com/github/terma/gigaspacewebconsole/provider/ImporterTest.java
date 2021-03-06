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

import com.gigaspaces.document.SpaceDocument;
import com.github.terma.gigaspacewebconsole.core.ExportRequest;
import com.github.terma.gigaspacewebconsole.core.ImportRequest;
import com.github.terma.gigaspacewebconsole.provider.driver.GigaSpaceUtils;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openspaces.core.GigaSpace;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.zip.ZipInputStream;

public class ImporterTest extends TestWithGigaSpace {

    private GigaSpace exportGigaSpace;
    private byte[] zip;
    private byte[] aSer;

    @Before
    public void before() throws Exception {
        exportGigaSpace = gigaSpace;

        GigaSpaceUtils.registerType(gigaSpace, "A");
        GigaSpaceUtils.writeDocument(gigaSpace, "A");
        GigaSpaceUtils.writeDocument(gigaSpace, "A");
        Assert.assertEquals(2, gigaSpace.count(new SpaceDocument("A")));

        GigaSpaceUtils.registerType(gigaSpace, "B");
        GigaSpaceUtils.writeDocument(gigaSpace, "B");
        Assert.assertEquals(1, gigaSpace.count(new SpaceDocument("B")));

        GigaSpaceUtils.registerType(gigaSpace, "C");

        ExportRequest request = new ExportRequest();
        request.url = gigaSpaceUrl;

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
    public void shouldImportFromZipWithSerFilesByDefault() throws Exception {
        final GigaSpace gigaSpace = GigaSpaceUtils.getGigaSpace("/./import-from-zip");
        GigaSpaceUtils.registerType(gigaSpace, "A");

        // given
        ImportRequest request = new ImportRequest();
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
