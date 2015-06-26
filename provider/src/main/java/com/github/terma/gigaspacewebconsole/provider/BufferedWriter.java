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

import org.openspaces.core.GigaSpace;

import java.util.ArrayList;
import java.util.List;

class BufferedWriter {

    private static final int BATCH = 1000;

    private final int batch;
    private final GigaSpace gigaSpace;
    private final List<Object> buffer = new ArrayList<>(BATCH);

    public BufferedWriter(final int batch, final GigaSpace gigaSpace) {
        this.batch = batch;
        this.gigaSpace = gigaSpace;
    }

    public BufferedWriter(final GigaSpace gigaSpace) {
        this(BATCH, gigaSpace);
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
