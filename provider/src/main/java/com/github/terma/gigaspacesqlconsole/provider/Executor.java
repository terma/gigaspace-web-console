package com.github.terma.gigaspacesqlconsole.provider;

import com.github.terma.gigaspacesqlconsole.core.ExecuteRequest;
import com.github.terma.gigaspacesqlconsole.core.ExecuteResponseStream;
import com.github.terma.gigaspacesqlconsole.core.config.Config;
import com.j_spaces.jdbc.driver.GConnection;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

public class Executor {

    private static final List<ExecutorPlugin> PLUGINS = Arrays.asList(
            new ExecutorPluginUpdate(),
            new ExecutorPluginGenerate()
    );

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
        for (final ExecutorPlugin plugin : PLUGINS) {
            if (plugin.execute(request, responseStream)) return;
        }
        handleOther(request, responseStream);
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
