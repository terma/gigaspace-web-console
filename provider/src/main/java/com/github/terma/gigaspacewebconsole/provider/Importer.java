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

import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;
import com.github.terma.gigaspacewebconsole.core.ImportRequest;
import org.openspaces.core.GigaSpace;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Importer {

    public static void execute(final ImportRequest request, final InputStream inputStream) throws Exception {
        final GigaSpace gigaSpace = GigaSpaceUtils.getGigaSpace(request);

        if (isSerFile(request)) {
            importOneType(inputStream, gigaSpace);
        } else {
            final ZipInputStream zipInputStream = new ZipInputStream(inputStream);
            while (true) {
                final ZipEntry zipEntry = zipInputStream.getNextEntry();
                if (zipEntry == null) break;
                importOneType(zipInputStream, gigaSpace);
            }
        }

        inputStream.close();
    }

    private static boolean isSerFile(final ImportRequest request) {
        return request.file != null && request.file.endsWith(".ser");
    }

    private static void importOneType(
            final InputStream inputStream, final GigaSpace gigaSpace)
            throws IOException, ClassNotFoundException {
        final ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

        final TypeDescriptor typeDescriptor = (TypeDescriptor) objectInputStream.readObject();
        if (gigaSpace.getTypeManager().getTypeDescriptor(typeDescriptor.typeName) == null) {
            registerType(gigaSpace, typeDescriptor);
        } else {
            // todo log that registration skipped
        }

        writeObjects(gigaSpace, objectInputStream);
    }

    private static void writeObjects(
            final GigaSpace gigaSpace, final ObjectInputStream objectInputStream)
            throws IOException, ClassNotFoundException {
        final BufferedWriter bufferedWriter = new BufferedWriter(gigaSpace);
        try {
            Object o;
            while ((o = objectInputStream.readObject()) != null) bufferedWriter.write(o);
        } catch (EOFException exception) {
            // end, so just stop
        }
        bufferedWriter.flush();
    }

    private static void registerType(final GigaSpace gigaSpace, final TypeDescriptor typeDescriptor) {
        final SpaceTypeDescriptorBuilder typeBuilder = new SpaceTypeDescriptorBuilder(typeDescriptor.typeName);
        if (typeDescriptor.spaceIdProperty != null) {
            typeBuilder.idProperty(typeDescriptor.spaceIdProperty);
        }
        if (typeDescriptor.routingProperty != null) {
            typeBuilder.routingProperty(typeDescriptor.routingProperty);
        }
        gigaSpace.getTypeManager().registerTypeDescriptor(typeBuilder.create());
    }

}
