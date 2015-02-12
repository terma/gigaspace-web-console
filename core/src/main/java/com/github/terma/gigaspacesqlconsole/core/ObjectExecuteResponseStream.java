package com.github.terma.gigaspacesqlconsole.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ObjectExecuteResponseStream implements ExecuteResponseStream {

    private List<String> columns;
    private List<List<String>> data;

    @Override
    public void writeHeader(List<String> columns) throws IOException {
        this.columns = columns;
    }

    @Override
    public void writeRow(List<String> values) throws IOException {
        if (data == null) data = new ArrayList<>();
        data.add(values);
    }

    @Override
    public void close() {

    }

    public List<String> getColumns() {
        return columns;
    }

    public List<List<String>> getData() {
        return data;
    }

}
