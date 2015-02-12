package com.github.terma.gigaspacesqlconsole.core;

public interface Provider {

    CountsResponse counts(CountsRequest request);

    void query(ExecuteRequest request, ExecuteResponseStream responseStream) throws Exception;

    CopyResponse copy(CopyRequest request) throws Exception;

}
