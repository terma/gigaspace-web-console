package com.github.terma.gigaspacesqlconsole.core;

import java.io.IOException;
import java.util.List;

public interface GroovyExecuteResponseStream {

    void startResult(String header) throws IOException;

    void writeColumns(List<String> columns) throws IOException;

    void writeRow(List<String> values) throws IOException;

    void closeResult() throws IOException;

    void close() throws IOException;

}
