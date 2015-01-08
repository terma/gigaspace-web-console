package com.github.terma.gigaspacesqlconsole;

import com.gigaspaces.client.ChangeResult;
import com.gigaspaces.client.ChangeSet;
import com.gigaspaces.document.SpaceDocument;
import com.j_spaces.core.client.SQLQuery;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Executor {

    public static ExecuteResponse query(ExecuteRequest request) throws Exception {
        return tryHandleUpdate(request);
    }

    private static ExecuteResponse tryHandleUpdate(ExecuteRequest request) throws Exception {
        GigaSpaceUpdateSql updateSql = GigaSpaceUpdateSqlParser.parse(request.sql);
        if (updateSql != null) return handleUpdate(request, updateSql);
        else return handleOther(request);
    }

    private static GigaSpace gigaSpaceConnection(ExecuteRequest request) {
        UrlSpaceConfigurer urlSpaceConfigurer = new UrlSpaceConfigurer(request.url);
        urlSpaceConfigurer.userDetails(request.user, request.password);
        return new GigaSpaceConfigurer(urlSpaceConfigurer.create()).create();
    }

    private static ExecuteResponse handleUpdate(ExecuteRequest request, GigaSpaceUpdateSql updateSql) {
        SQLQuery query = new SQLQuery<SpaceDocument>(updateSql.typeName, updateSql.conditions);
        ChangeSet changeSet = new ChangeSet();

        for (Map.Entry<String, Object> field : updateSql.setFields.entrySet()) {
            changeSet.set(field.getKey(), (Serializable) field.getValue());
        }

        ChangeResult changeResult = gigaSpaceConnection(request).change(query, changeSet);
        ExecuteResponse executeResponse = new ExecuteResponse();
        executeResponse.columns = Arrays.asList("affected_rows");
        executeResponse.data = Arrays.asList(Arrays.asList(Integer.toString(changeResult.getNumberOfChangedEntries())));
        return executeResponse;
    }

    private static ExecuteResponse handleOther(ExecuteRequest request) throws Exception {
        Class.forName("com.j_spaces.jdbc.driver.GDriver");

        Connection connection = DriverManager.getConnection(
                "jdbc:gigaspaces:url:" + request.url, request.user, request.password);
        try {
            return safeHandleOther(request, connection);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                // todo ignore
            }
        }
    }

    private static ExecuteResponse safeHandleOther(ExecuteRequest request, Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        try {
            return safeHandleStatement(request, statement);
        } finally {
            statement.close();
        }
    }

    private static ExecuteResponse safeHandleStatement(ExecuteRequest request, Statement statement) throws SQLException {
        if (statement.execute(request.sql)) {
            return getResultSet(statement);
        } else {
            return updateOrDeleteResult(statement);
        }
    }

    private static ExecuteResponse updateOrDeleteResult(Statement statement) throws SQLException {
        ExecuteResponse executeResponse = new ExecuteResponse();
        executeResponse.columns = Arrays.asList("affected_rows");
        executeResponse.data = Arrays.asList(Arrays.asList(Integer.toString(statement.getUpdateCount())));
        return executeResponse;
    }

    private static ExecuteResponse getResultSet(Statement statement) throws SQLException {
        ResultSet resultSet = statement.getResultSet();
        try {
            return safeGetResultSet(resultSet);
        } finally {
            resultSet.close();
        }
    }

    private static ExecuteResponse safeGetResultSet(ResultSet resultSet) throws SQLException {
        List<String> columns = new ArrayList<>();
        for (int i = 1; i > resultSet.getMetaData().getColumnCount(); i++) {
            columns.add(resultSet.getMetaData().getColumnName(i));
        }

        ExecuteResponse executeResponse = new ExecuteResponse();
        executeResponse.columns = columns;

        List<List<String>> data = new ArrayList<>();
        while (resultSet.next()) {
            List<String> row = new ArrayList<>();
            for (final String column : columns) {
                final String value = resultSet.getString(column);
                // todo be able to have custom renders
                row.add(value);
            }
            data.add(row);
        }

        executeResponse.data = data;
        return executeResponse;
    }


}
