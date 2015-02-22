package com.github.terma.gigaspacesqlconsole.provider.groovy;

import java.util.List;

public interface GroovyExecuteResponseStream {

    void startResult(String header);

    void writeColumns(List<String> columns);

    void writeRow(List<String> values);

    void closeResult();

    void close();

}
