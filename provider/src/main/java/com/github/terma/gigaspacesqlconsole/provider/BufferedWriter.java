package com.github.terma.gigaspacesqlconsole.provider;

import org.openspaces.core.GigaSpace;

import java.util.ArrayList;
import java.util.List;

class BufferedWriter {

    private static final int BATCH = 1000;

    private final GigaSpace gigaSpace;
    private final List<Object> buffer = new ArrayList<>(BATCH);

    public BufferedWriter(final GigaSpace gigaSpace) {
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
