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
