/*
Copyright 2015 Artem Stasiuk

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

package com.github.terma.gigaspacewebconsole.provider.executor;

import com.github.terma.gigaspacewebconsole.core.ExecuteRequest;
import com.github.terma.gigaspacewebconsole.core.ExecuteResponseStream;
import com.github.terma.gigaspacewebconsole.provider.ConverterHelper;
import com.github.terma.gigaspacewebconsole.provider.RealSqlResult;
import com.github.terma.gigaspacewebconsole.provider.SqlResult;
import com.github.terma.gigaspacewebconsole.provider.groovy.UpdateSqlResult;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Executor {

    private final ConnectionFactory connectionFactory;
    private final ExecutorPreprocessor preprocessor;
    private final List<ExecutorPlugin> plugins;
    private final ConverterHelper converterHelper;

    public Executor(
            final ConnectionFactory connectionFactory, final ExecutorPreprocessor preprocessor,
            final List<ExecutorPlugin> plugins, final ConverterHelper converterHelper) {
        this.connectionFactory = connectionFactory;
        this.preprocessor = preprocessor;
        this.plugins = plugins;
        this.converterHelper = converterHelper;
    }

    private static void sqlResultToResponseStream
            (final SqlResult sqlResult, final ExecuteResponseStream responseStream)
            throws IOException, SQLException {
        responseStream.writeHeader(sqlResult.getColumns());
        while (sqlResult.next()) responseStream.writeRow(sqlResult.getRow(), sqlResult.getRowTypes());
        responseStream.close();
    }

    public void execute(final ExecuteRequest request, final ExecuteResponseStream responseStream) throws Exception {
        request.sql = preprocessor.preprocess(request.sql);

        for (final ExecutorPlugin plugin : plugins) {
            if (plugin.execute(request, responseStream)) return;
        }

        originalExecute(request, responseStream);
    }

    private void originalExecute(ExecuteRequest request, ExecuteResponseStream responseStream) throws Exception {
        try (final SqlResult sqlResult = originalExecute(request)) {
            sqlResultToResponseStream(sqlResult, responseStream);
        }
    }

    public SqlResult originalExecute(final ExecuteRequest request) throws Exception {
        final Connection connection = connectionFactory.get(request);
        final Statement statement = connection.createStatement();
        if (statement.execute(request.sql)) {
            return new RealSqlResult(statement.getResultSet(), request.sql, converterHelper);
        } else {
            return new UpdateSqlResult(statement, request.sql);
        }
    }

}
