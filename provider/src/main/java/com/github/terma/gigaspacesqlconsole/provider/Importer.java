package com.github.terma.gigaspacesqlconsole.provider;

import com.github.terma.gigaspacesqlconsole.core.ImportRequest;
import org.openspaces.core.GigaSpace;

import java.io.EOFException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class Importer {

    private final static int BUFFER_SIZE = 1000;

    public static void execute(final ImportRequest request, final InputStream inputStream) throws Exception {
        final GigaSpace gigaSpace = GigaSpaceUtils.getGigaSpace(request);

        final ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

        try {
            Object o;
            while ((o = objectInputStream.readObject()) != null) {
                gigaSpace.write(o);
            }
        } catch (EOFException exception) {
            // end, so just stop
        }

        objectInputStream.close();
    }

}
