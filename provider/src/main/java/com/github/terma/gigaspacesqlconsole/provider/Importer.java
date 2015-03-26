package com.github.terma.gigaspacesqlconsole.provider;

import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;
import com.github.terma.gigaspacesqlconsole.core.ImportRequest;
import org.openspaces.core.GigaSpace;

import java.io.EOFException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class Importer {

    private final static int BATCH = 1000;

    public static void execute(final ImportRequest request, final InputStream inputStream) throws Exception {
        final GigaSpace gigaSpace = GigaSpaceUtils.getGigaSpace(request);

        final ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

        final TypeDescriptor typeDescriptor = (TypeDescriptor) objectInputStream.readObject();
        if (gigaSpace.getTypeManager().getTypeDescriptor(typeDescriptor.typeName) == null) {
            final SpaceTypeDescriptorBuilder typeBuilder = new SpaceTypeDescriptorBuilder(typeDescriptor.typeName);
            if (typeDescriptor.spaceIdProperty != null) {
                typeBuilder.idProperty(typeDescriptor.spaceIdProperty);
            }
            if (typeDescriptor.routingProperty != null) {
                typeBuilder.routingProperty(typeDescriptor.routingProperty);
            }
            gigaSpace.getTypeManager().registerTypeDescriptor(typeBuilder.create());
        } else {
            // todo log that registration skipped
        }

        final BufferedWriter bufferedWriter = new BufferedWriter(gigaSpace);
        try {
            Object o;
            while ((o = objectInputStream.readObject()) != null) bufferedWriter.write(o);
        } catch (EOFException exception) {
            // end, so just stop
        }
        bufferedWriter.flush();

        objectInputStream.close();
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
            gigaSpace.writeMultiple(buffer.toArray());
            buffer.clear();
        }

    }

}
