package com.github.terma.gigaspacesqlconsole.provider;

import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;
import com.github.terma.gigaspacesqlconsole.core.ImportRequest;
import org.openspaces.core.GigaSpace;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Importer {

    private final static int BATCH = 1000;

    public static void execute(final ImportRequest request, final InputStream inputStream) throws Exception {
        final GigaSpace gigaSpace = GigaSpaceUtils.getGigaSpace(request);

        if (zipFileForImport(request)) {
            final ZipInputStream zipInputStream = new ZipInputStream(inputStream);
            while (true) {
                final ZipEntry zipEntry = zipInputStream.getNextEntry();
                if (zipEntry == null) break;
                importOneType(zipInputStream, gigaSpace);
            }
        } else {
            importOneType(inputStream, gigaSpace);
        }

        inputStream.close();
    }

    private static boolean zipFileForImport(final ImportRequest request) {
        return request.file.endsWith(".zip");
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

    private static class BufferedWriter {

        private final GigaSpace gigaSpace;
        private final List<Object> buffer = new ArrayList<>(BATCH);

        private BufferedWriter(final GigaSpace gigaSpace) {
            this.gigaSpace = gigaSpace;
        }

        public void write(Object object) {
            buffer.add(object);
            if (buffer.size() >= BATCH) flush();
        }

        public void flush() {
            if (buffer.size() > 0) gigaSpace.writeMultiple(buffer.toArray());
            buffer.clear();
        }

    }

}
