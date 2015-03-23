package com.github.terma.gigaspacesqlconsole.core;

import java.io.InputStream;

public interface Provider {

    CountsResponse counts(CountsRequest request);

    void query(ExecuteRequest request, ExecuteResponseStream responseStream) throws Exception;

    CopyResponse copy(CopyRequest request) throws Exception;

    void execute(ExecuteRequest request, GroovyExecuteResponseStream responseStream) throws Exception;

    void import1(ImportRequest request, InputStream inputStream) throws Exception;

}
