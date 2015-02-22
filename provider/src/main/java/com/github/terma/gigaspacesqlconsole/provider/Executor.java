package com.github.terma.gigaspacesqlconsole.provider;

import com.github.terma.gigaspacesqlconsole.core.ExecuteRequest;
import com.github.terma.gigaspacesqlconsole.core.ExecuteResponseStream;
import com.github.terma.gigaspacesqlconsole.provider.groovy.RealSqlResult;
import com.github.terma.gigaspacesqlconsole.provider.groovy.SqlResult;
import com.github.terma.gigaspacesqlconsole.provider.groovy.UpdateSqlResult;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class Executor {

    private static final List<ExecutorPlugin> PLUGINS = Arrays.asList(
            new ExecutorPluginUpdate(),
            new ExecutorPluginGenerate()
    );

    public static void query(final ExecuteRequest request, final ExecuteResponseStream responseStream) throws Exception {
        for (final ExecutorPlugin plugin : PLUGINS) {
            if (plugin.execute(request, responseStream)) return;
        }

        try (final SqlResult sqlResult = handleOther(request)) {
            sqlResultToResponseStream(sqlResult, responseStream);
        }
    }

    public static SqlResult execute(final ExecuteRequest request) throws Exception {
        final Connection connection = GigaSpaceUtils.createJdbcConnection(request);
        final Statement statement = connection.createStatement();
        if (statement.execute(request.sql)) {
            return new RealSqlResult(statement);
        } else {
            return new UpdateSqlResult(statement);
        }
    }

    private static SqlResult handleOther(final ExecuteRequest request) throws Exception {
        Connection connection = GigaSpaceUtils.createJdbcConnection(request);
        final Statement statement = connection.createStatement();
        if (statement.execute(request.sql)) {
            return new RealSqlResult(statement);
        } else {
            return new UpdateSqlResult(statement);
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
