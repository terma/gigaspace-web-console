package com.github.terma.gigaspacesqlconsole;

import com.gigaspaces.client.ChangeResult;
import com.gigaspaces.client.ChangeSet;
import com.gigaspaces.document.SpaceDocument;
import com.github.terma.gigaspacesqlconsole.config.Config;
import com.j_spaces.core.client.SQLQuery;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Executor {

    private static final String CONVERTER_METHOD = "convert";

    private static final List<Method> converterMethods = new ArrayList<>();

    static {
        final Config config = Config.read();

        for (final String converterClassName : config.converters) {
            final Class converterClass;
            try {
                converterClass = Class.forName(converterClassName);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Can't load converter class: " + converterClassName);
            }

            final Method method;
            try {
                method = converterClass.getMethod(CONVERTER_METHOD, Object.class);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("Can't find converter method: " + CONVERTER_METHOD +
                        " in class: " + converterClassName + " with one Object type parameter!");
            }

            converterMethods.add(method);
        }
    }

    public static ExecuteResponse query(ExecuteRequest request) throws Exception {
        if ("create test space".equals(request.sql)) {
            return createTestSpace(request);
        } else {
            final GigaSpaceUpdateSql updateSql = GigaSpaceUpdateSqlParser.parse(request.sql);
            if (updateSql != null) return handleUpdate(request, updateSql);
            else return handleOther(request);
        }
    }

    private static ExecuteResponse createTestSpace(ExecuteRequest request) {
        request.password = null;
        request.user = null;

        final GigaSpace gigaSpace = Executor.gigaSpaceConnection(request);

        // register test type
        GigaSpaceUtils.registerType(gigaSpace, "mumba");

        // create mock data
        GigaSpaceUtils.createDocument(gigaSpace, "mumba");

        ExecuteResponse executeResponse = new ExecuteResponse();
        executeResponse.columns = new ArrayList<>();
        executeResponse.data = new ArrayList<>();
        return executeResponse;
    }

    // todo only for test fix that
    public static GigaSpace gigaSpaceConnection(ExecuteRequest request) {
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
        for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
            columns.add(resultSet.getMetaData().getColumnName(i));
        }

        ExecuteResponse executeResponse = new ExecuteResponse();
        executeResponse.columns = columns;

        List<List<String>> data = new ArrayList<>();
        while (resultSet.next()) {
            List<String> row = new ArrayList<>();
            for (final String column : columns) {
                final Object rawValue = resultSet.getObject(column);
                String value = null;

                for (final Method convertMethod : converterMethods) {
                    try {
                        value = (String) convertMethod.invoke(null, rawValue);
                    } catch (IllegalAccessException e) {
                        throw new IllegalArgumentException("Can't convert!", e);
                    } catch (InvocationTargetException e) {
                        throw new IllegalArgumentException("Can't convert!", e);
                    }

                    if (value != null) break;
                }

                if (value == null) value = resultSet.getString(column);

                row.add(value);
            }
            data.add(row);
        }

        executeResponse.data = data;
        return executeResponse;
    }


}
