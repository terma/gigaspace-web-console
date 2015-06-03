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

package com.github.terma.gigaspacesqlconsole;

import com.github.terma.gigaspacesqlconsole.core.GroovyExecuteResponseStream;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class JsonGroovyExecuteResponseStream implements GroovyExecuteResponseStream {

    private final JsonWriter jsonWriter;

    public JsonGroovyExecuteResponseStream(final Writer writer) throws IOException {
        jsonWriter = new JsonWriter(writer);
        jsonWriter.beginArray();
    }

    @Override
    public void startResult(final String header) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("header").value(header);
    }

    @Override
    public void writeColumns(List<String> columns) throws IOException {
        jsonWriter.name("columns");
        writeArray(columns);

        jsonWriter.name("data");
        jsonWriter.beginArray();
    }

    @Override
    public void writeRow(List<String> values) throws IOException {
        writeArray(values);
    }

    @Override
    public void closeResult() throws IOException {
        jsonWriter.endArray();
        jsonWriter.endObject();
    }

    private void writeArray(List<String> values) throws IOException {
        jsonWriter.beginArray();
        for (final String value : values) jsonWriter.value(value);
        jsonWriter.endArray();
    }

    @Override
    public void close() throws IOException {
        jsonWriter.endArray();
    }

}
