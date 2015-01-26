package com.github.terma.gigaspacesqlconsole.provider;

import com.github.terma.gigaspacesqlconsole.core.*;

public class ProviderImpl implements Provider {

    private CountsProviderImpl countsProvider = new CountsProviderImpl();

    @Override
    public CountsResponse counts(CountsRequest request) {
        return countsProvider.counts(request);
    }

    @Override
    public ExecuteResponse query(ExecuteRequest request) throws Exception {
        return Executor.query(request);
    }

}
