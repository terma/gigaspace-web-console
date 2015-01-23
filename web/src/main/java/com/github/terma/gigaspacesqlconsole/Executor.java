package com.github.terma.gigaspacesqlconsole;

import com.github.terma.gigaspacesqlconsole.config.Config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Executor {

    private static final String CONVERTER_METHOD = "convert";

    private static final List<Method> converterMethods = new ArrayList<>();

    private static final ExecutorProvider EXECUTOR_PROVIDER = null;

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

    public static ExecuteResponse query(ExecuteRequest request) throws Exception {
        final GigaSpaceUpdateSql updateSql = GigaSpaceUpdateSqlParser.parse(request.sql);
        if (updateSql != null) return handleUpdate(request, updateSql);
        else return handleOther(request);
    }

    private static ExecuteResponse handleUpdate(ExecuteRequest request, GigaSpaceUpdateSql updateSql) {
        return EXECUTOR_PROVIDER.handleUpdate(request, updateSql);
    }

    private static ExecuteResponse handleOther(ExecuteRequest request) throws Exception {
        Connection connection = EXECUTOR_PROVIDER.getConnection(request);
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
