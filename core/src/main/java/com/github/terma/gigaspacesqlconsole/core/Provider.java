package com.github.terma.gigaspacesqlconsole.core;

import java.io.InputStream;
import java.io.OutputStream;

public interface Provider {

    CountsResponse counts(CountsRequest request);

    void query(ExecuteRequest request, ExecuteResponseStream responseStream) throws Exception;

    CopyResponse copy(CopyRequest request) throws Exception;

    void execute(ExecuteRequest request, GroovyExecuteResponseStream responseStream) throws Exception;

    void export(ExportRequest request, OutputStream outputStream) throws Exception;

    void import1(ImportRequest request, InputStream inputStream) throws Exception;

}
