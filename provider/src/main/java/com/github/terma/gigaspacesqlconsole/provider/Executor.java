package com.github.terma.gigaspacesqlconsole.provider;

import com.gigaspaces.client.ChangeResult;
import com.gigaspaces.client.ChangeSet;
import com.github.terma.gigaspacesqlconsole.core.ExecuteRequest;
import com.github.terma.gigaspacesqlconsole.core.ExecuteResponseStream;
import com.github.terma.gigaspacesqlconsole.core.config.Config;
import com.j_spaces.core.client.SQLQuery;
import com.j_spaces.jdbc.driver.GConnection;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class Executor {

    private static final String CONVERTER_METHOD = "convert";

    private static final List<Method> converterMethods = new ArrayList<>();

    static {
        final Config config = Config.read();

        for (final String converterClassName : config.user.converters) {
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

    public static void query(final ExecuteRequest request, final ExecuteResponseStream responseStream) throws Exception {
        final GigaSpaceUpdateSql updateSql = GigaSpaceUpdateSqlParser.parse(request.sql);
        if (updateSql != null) handleUpdate(request, updateSql, responseStream);
        else handleOther(request, responseStream);
    }

    public static Connection getConnection(final ExecuteRequest request) throws SQLException, ClassNotFoundException {
        java.util.Properties info = new java.util.Properties();

        if (request.user != null) {
            info.put("user", request.user);
        }
        if (request.password != null) {
            info.put("password", request.password);
        }

        return new GConnection(GConnection.JDBC_GIGASPACES_URL + request.url, info);
    }

    @SuppressWarnings("deprecation")
    private static GigaSpace gigaSpaceConnection(ExecuteRequest request) {
        UrlSpaceConfigurer urlSpaceConfigurer = new UrlSpaceConfigurer(request.url);
        urlSpaceConfigurer.userDetails(request.user, request.password);
        return new GigaSpaceConfigurer(urlSpaceConfigurer.create()).create();
    }

    private static void handleUpdate(
            final ExecuteRequest request, GigaSpaceUpdateSql updateSql,
            final ExecuteResponseStream responseStream) throws IOException {
        SQLQuery query = new SQLQuery<>(updateSql.typeName, updateSql.conditions);
        ChangeSet changeSet = new ChangeSet();

        for (Map.Entry<String, Object> field : updateSql.setFields.entrySet()) {
            changeSet.set(field.getKey(), (Serializable) field.getValue());
        }

        ChangeResult changeResult = gigaSpaceConnection(request).change(query, changeSet);
        responseStream.writeHeader(asList("affected_rows"));
        responseStream.writeRow(asList(Integer.toString(changeResult.getNumberOfChangedEntries())));
        responseStream.close();
    }

    private static void handleOther(
            final ExecuteRequest request, final ExecuteResponseStream responseStream) throws Exception {
        Connection connection = getConnection(request);
        try {
            safeHandleOther(request, connection, responseStream);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                // todo ignore
            }
        }
    }

    private static void safeHandleOther(
            final ExecuteRequest request, final Connection connection,
            final ExecuteResponseStream responseStream) throws SQLException, IOException {
        try (Statement statement = connection.createStatement()) {
            safeHandleStatement(request, statement, responseStream);
        }
    }

    private static void safeHandleStatement(
            final ExecuteRequest request, final Statement statement,
            final ExecuteResponseStream responseStream) throws SQLException, IOException {
        if (statement.execute(request.sql)) {
            getResultSet(statement, responseStream);
        } else {
            updateOrDeleteResult(statement, responseStream);
        }
    }

    private static void updateOrDeleteResult(
            final Statement statement, final ExecuteResponseStream responseStream) throws SQLException, IOException {
        responseStream.writeHeader(asList("affected_rows"));
        responseStream.writeRow(asList(Integer.toString(statement.getUpdateCount())));
        responseStream.close();
    }

    private static void getResultSet(
            final Statement statement, final ExecuteResponseStream responseStream) throws SQLException, IOException {
        try (ResultSet resultSet = statement.getResultSet()) {
            safeGetResultSet(resultSet, responseStream);
        }
    }

    private static void safeGetResultSet(
            final ResultSet resultSet, final ExecuteResponseStream responseStream) throws SQLException, IOException {
        List<String> columns = new ArrayList<>();
        for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
            columns.add(resultSet.getMetaData().getColumnName(i));
        }
        responseStream.writeHeader(columns);

        while (resultSet.next()) {
            List<String> row = new ArrayList<>();
            for (final String column : columns) {
                String value = getFormattedValue(resultSet, column);

                row.add(value);
            }
            responseStream.writeRow(row);
        }

        responseStream.close();
    }

    private static String getFormattedValue(final ResultSet resultSet, final String column) throws SQLException {
        final Object rawValue = resultSet.getObject(column);

        for (final Method convertMethod : converterMethods) {
            try {
                final String value = (String) convertMethod.invoke(null, rawValue);
                if (value != null) return value;
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalArgumentException("Can't convert!", e);
            }
        }

        return resultSet.getString(column);
    }

}
