package com.github.terma.gigaspacesqlconsole.provider.groovy;

import java.util.ArrayList;
import java.util.List;

public class ObjectGroovyExecuteResponseStream implements GroovyExecuteResponseStream {

    public List<ObjectGroovyExecuteResult> results = new ArrayList<>();

    private ObjectGroovyExecuteResult currentResult;

    @Override
    public void startResult(String header) {
        currentResult = new ObjectGroovyExecuteResult();
        currentResult.header = header;
    }

    @Override
    public void writeColumns(List<String> columns) {
        currentResult.columns = columns;
    }

    @Override
    public void writeRow(List<String> values) {
        currentResult.data.add(values);
    }

    @Override
    public void closeResult() {
        results.add(currentResult);
        currentResult = null;
    }

    @Override
    public void close() {

    }

}
