/*
Copyright 2015-2017 Artem Stasiuk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.github.terma.gigaspacewebconsole.provider.groovy;

import com.github.terma.gigaspacewebconsole.core.ExecuteRequest;
import com.github.terma.gigaspacewebconsole.provider.SqlResult;
import com.github.terma.gigaspacewebconsole.provider.executor.gigaspace.GigaSpaceExecutor;
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
    public Object call(Object argument) {
        if (argument == null) throw new NullPointerException("Can't sql null!");

        final ExecuteRequest concreteRequest = new ExecuteRequest();
        concreteRequest.driver = request.driver;
        concreteRequest.url = request.url;
        concreteRequest.user = request.user;
        concreteRequest.password = request.password;

        concreteRequest.sql = argument.toString();

        try {
            final SqlResult sqlResult = GigaSpaceExecutor.INSTANCE.originalExecute(concreteRequest);
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
