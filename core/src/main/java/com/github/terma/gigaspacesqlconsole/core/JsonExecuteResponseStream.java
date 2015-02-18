package com.github.terma.gigaspacesqlconsole.core;

import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class JsonExecuteResponseStream implements ExecuteResponseStream {

    private final JsonWriter jsonWriter;

    public JsonExecuteResponseStream(Writer writer) {
        jsonWriter = new JsonWriter(writer);

    }

    @Override
    public void writeHeader(List<String> columns) throws IOException {
        jsonWriter.beginObject();

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
    public void close() throws IOException {
        jsonWriter.endArray();
        jsonWriter.endObject();
    }

    private void writeArray(List<String> values) throws IOException {
        jsonWriter.beginArray();
        for (final String value : values) jsonWriter.value(value);
        jsonWriter.endArray();
    }

}
