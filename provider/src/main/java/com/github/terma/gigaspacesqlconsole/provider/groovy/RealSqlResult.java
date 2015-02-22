package com.github.terma.gigaspacesqlconsole.provider.groovy;

import com.github.terma.gigaspacesqlconsole.provider.ConverterHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RealSqlResult implements SqlResult {

    private final ResultSet resultSet;
    private final List<String> columns;
    private final String sql;

    public RealSqlResult(final Statement statement, final String sql) throws SQLException {
        this.sql = sql;
        this.resultSet = statement.getResultSet();

        columns = new ArrayList<>();
        for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
            columns.add(resultSet.getMetaData().getColumnName(i));
        }
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public boolean next() throws SQLException {
        return resultSet.next();
    }

    public List<String> getColumns() throws SQLException {
        return columns;
    }

    public List<String> getRow() throws SQLException {
        List<String> row = new ArrayList<>();
        for (final String column : columns) {
            row.add(ConverterHelper.getFormattedValue(resultSet, column));
        }
        return row;
    }

    public void close() throws SQLException {
        final Statement statement = resultSet.getStatement();
        final Connection connection = statement.getConnection();

        resultSet.close();
        statement.close();
        connection.close();
    }

    @Override
    public String getSql() {
        return sql;
    }

}
