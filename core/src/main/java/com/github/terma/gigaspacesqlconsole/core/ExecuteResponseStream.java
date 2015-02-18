package com.github.terma.gigaspacesqlconsole.core;

import java.io.IOException;
import java.util.List;

public interface ExecuteResponseStream {

    void writeHeader(List<String> columns) throws IOException;

    void writeRow(List<String> values) throws IOException;

    void close() throws IOException;

}
