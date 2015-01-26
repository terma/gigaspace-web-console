package com.github.terma.gigaspacesqlconsole.core;

public interface Provider {

    CountsResponse counts(CountsRequest request);

    ExecuteResponse query(ExecuteRequest request) throws Exception;

}
