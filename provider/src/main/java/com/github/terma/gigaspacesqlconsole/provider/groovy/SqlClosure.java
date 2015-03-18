package com.github.terma.gigaspacesqlconsole.provider.groovy;

import com.github.terma.gigaspacesqlconsole.core.ExecuteRequest;
import com.github.terma.gigaspacesqlconsole.provider.Executor;
import groovy.lang.Closure;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class SqlClosure extends Closure {

    private final ExecuteRequest request;
    private final List<SqlResult> sqlResults = new LinkedList<>();

    public SqlClosure(final ExecuteRequest request) {
        super(null);
        this.request = request;
    }

    @Override
    public Object call(Object arguments) {
        if (arguments == null) throw new NullPointerException("Can't sql null!");

        final ExecuteRequest concreteRequest = new ExecuteRequest();
        concreteRequest.gs = request.gs;
        concreteRequest.url = request.url;
        concreteRequest.user = request.user;
        concreteRequest.password = request.password;

        concreteRequest.sql = arguments.toString();

        try {
            final SqlResult sqlResult = Executor.execute(concreteRequest);
            sqlResults.add(sqlResult);
            return sqlResult;
        } catch (Exception exception) {
            throw new RuntimeException("Can't sql!", exception);
        }
    }

    public void close() {
        final List<SqlResult> sqlResultsForClose = sqlResults;
        sqlResults.clear();

        for (final SqlResult sqlResult : sqlResultsForClose) {
            try {
                sqlResult.close();
            } catch (SQLException exception) {
                // skip result
            }
        }
    }

}
