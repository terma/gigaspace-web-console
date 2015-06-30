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
import com.github.terma.gigaspacewebconsole.provider.GigaSpaceUtils;
import com.github.terma.gigaspacewebconsole.provider.groovy.RealSqlResult;
import com.github.terma.gigaspacewebconsole.provider.groovy.SqlResult;
import com.github.terma.gigaspacewebconsole.provider.groovy.UpdateSqlResult;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class Executor {

    private static final ExecutorPreprocessor PREPROCESSOR = new TimestampPreprocessor();

    private static final List<ExecutorPlugin> PLUGINS = Arrays.asList(
            new PropertySelectExecutorPlugin(),
            new ExecutorPluginUpdate(),
            new ExecutorPluginGenerate()
    );

    public static void execute(final ExecuteRequest request, final ExecuteResponseStream responseStream) throws Exception {
        request.sql = PREPROCESSOR.preprocess(request.sql);

        for (final ExecutorPlugin plugin : PLUGINS) {
            if (plugin.execute(request, responseStream)) return;
        }

        originalExecute(request, responseStream);
    }

    private static void originalExecute(ExecuteRequest request, ExecuteResponseStream responseStream) throws Exception {
        try (final SqlResult sqlResult = originalExecute(request)) {
            sqlResultToResponseStream(sqlResult, responseStream);
        }
    }

    public static SqlResult originalExecute(final ExecuteRequest request) throws Exception {
        final Connection connection = GigaSpaceUtils.createJdbcConnection(request);
        final Statement statement = connection.createStatement();
        if (statement.execute(request.sql)) {
            return new RealSqlResult(statement, request.sql);
        } else {
            return new UpdateSqlResult(statement, request.sql);
        }
    }

    private static void sqlResultToResponseStream
            (final SqlResult sqlResult, final ExecuteResponseStream responseStream)
            throws IOException, SQLException {
        responseStream.writeHeader(sqlResult.getColumns());
        while (sqlResult.next()) responseStream.writeRow(sqlResult.getRow());
        responseStream.close();
    }

}