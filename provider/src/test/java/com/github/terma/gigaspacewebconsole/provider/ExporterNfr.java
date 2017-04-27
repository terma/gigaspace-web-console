/*
Copyright 2015-2016 Artem Stasiuk

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
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;
import com.github.terma.gigaspacewebconsole.core.ExportRequest;
import com.github.terma.gigaspacewebconsole.provider.driver.GigaSpaceUtils;
import org.openspaces.core.GigaSpace;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;

public class ExporterNfr {

    public static void main(String[] args) throws Exception {
        final String typeName = "TestExportType";
        final int count = 1000 * 1000;

        final File file = File.createTempFile("gigaspace-web-console-exporter-test", "zip");
        file.deleteOnExit();

        String url = "jini:/*/*/gs10?locators=127.0.0.1:4700";
        System.out.println("Connecting to " + url + "...");
        GigaSpace gigaSpace = GigaSpaceUtils.getGigaSpace(url);

        if (gigaSpace.getTypeManager().getTypeDescriptor(typeName) == null) {
            System.out.println("Register type...");
            SpaceTypeDescriptor typeDescriptor =
                    new SpaceTypeDescriptorBuilder(typeName)
                            .idProperty("id", true).create();
            gigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);
        }

        System.out.println("Load x" + count + " data...");
        gigaSpace.clear(new SpaceDocument(typeName));
        final BufferedWriter bufferedWriter = new BufferedWriter(gigaSpace);
        for (int i = 0; i < count; i++) {
            SpaceDocument document = new SpaceDocument(typeName);
            document.setProperty("name", "test object name " + i);
            document.setProperty("pseudo_number", i);
            document.setProperty("creation_time", new Date());
            document.setProperty("value", new BigDecimal(System.currentTimeMillis()));
            bufferedWriter.write(document);
        }
        bufferedWriter.flush();

        System.out.println("Export...");

        ExportRequest exportRequest = new ExportRequest();
        exportRequest.url = url;

        try (OutputStream outputStream = new FileOutputStream(file)) {
            Exporter.execute(exportRequest, outputStream);
        }

        System.out.println("Test done, file size: " + (file.length() / 1024 / 1024) + " mb");
    }

}
