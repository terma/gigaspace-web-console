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

package com.github.terma.gigaspacewebconsole.core;

import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class JsonExecuteResponseStream implements ExecuteResponseStream {

    private final JsonWriter jsonWriter;

    public JsonExecuteResponseStream(final Writer writer) {
        jsonWriter = new JsonWriter(writer);
    }

    @Override
    public void writeHeader(final List<String> columns) throws IOException {
        jsonWriter.beginObject();

        jsonWriter.name("columns");
        writeArray(columns);

        jsonWriter.name("data");
        jsonWriter.beginArray();
    }

    @Override
    public void writeRow(final List<String> values, final List<String> types) throws IOException {
        jsonWriter.beginArray();
        writeArrayItems(values);
        writeArray(types);
        jsonWriter.endArray();
    }

    @Override
    public void close() throws IOException {
        jsonWriter.endArray();
        jsonWriter.endObject();
    }

    private void writeArray(final List<String> values) throws IOException {
        jsonWriter.beginArray();
        writeArrayItems(values);
        jsonWriter.endArray();
    }

    private void writeArrayItems(final List<String> values) throws IOException {
        for (final String value : values) jsonWriter.value(value);
    }

}
