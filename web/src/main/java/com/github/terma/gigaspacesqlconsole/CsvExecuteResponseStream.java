package com.github.terma.gigaspacesqlconsole;

import com.github.terma.gigaspacesqlconsole.core.ExecuteResponseStream;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import static com.github.terma.gigaspacesqlconsole.StringUtils.toCsvRow;

public class CsvExecuteResponseStream implements ExecuteResponseStream {

    private final Writer writer;

    public CsvExecuteResponseStream(final Writer writer) {
        this.writer = writer;
    }

    @Override
    public void writeHeader(List<String> columns) throws IOException {
        writeRow(columns);
    }

    @Override
    public void writeRow(List<String> values) throws IOException {
        writer.append(toCsvRow(values)).append('\n');
    }

    @Override
    public void close() {

    }

}
