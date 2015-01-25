package com.github.terma.gigaspacesqlconsole.provider;

import com.github.terma.gigaspacesqlconsole.core.*;

import java.sql.Connection;
import java.sql.SQLException;

public class ProviderImpl implements Provider {

    private CountsProviderImpl countsProvider = new CountsProviderImpl();
    private ExecutorProviderImpl executorProvider = new ExecutorProviderImpl();

    @Override
    public CountsResponse counts(CountsRequest request) {
        return countsProvider.counts(request);
    }

    @Override
    public ExecuteResponse handleUpdate(ExecuteRequest request, GigaSpaceUpdateSql updateSql) {
        return executorProvider.handleUpdate(request, updateSql);
    }

    @Override
    public Connection getConnection(final ExecuteRequest request) throws SQLException, ClassNotFoundException {
        return executorProvider.getConnection(request);
    }

}
