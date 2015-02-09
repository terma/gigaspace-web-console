package com.github.terma.gigaspacesqlconsole.provider;

import com.github.terma.gigaspacesqlconsole.core.*;

public class ProviderImpl implements Provider {

    private Counts countsProvider = new Counts();

    @Override
    public CountsResponse counts(CountsRequest request) {
        return countsProvider.counts(request);
    }

    @Override
    public ExecuteResponse query(ExecuteRequest request) throws Exception {
        return Executor.query(request);
    }

    @Override
    public CopyResponse copy(final CopyRequest request) throws Exception {
        return Copier.copy(request);
    }

}
