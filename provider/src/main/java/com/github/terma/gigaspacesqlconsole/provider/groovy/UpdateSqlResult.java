package com.github.terma.gigaspacesqlconsole.provider.groovy;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class UpdateSqlResult implements SqlResult {

    private final Statement statement;
    private final int affectedRow;
    private final String sql;
    private boolean next = true;

    public UpdateSqlResult(final Statement statement, final String sql) throws SQLException {
        this.statement = statement;
        this.sql = sql;
        this.affectedRow = statement.getUpdateCount();
    }

    public boolean next() throws SQLException {
        boolean currentNext = next;
        next = false;
        return currentNext;
    }

    public List<String> getColumns() throws SQLException {
        return Arrays.asList("affected_rows");
    }

    public List<String> getRow() throws SQLException {
        return Arrays.asList(Integer.toString(affectedRow));
    }

    public void close() throws SQLException {
        final Connection connection = statement.getConnection();

        statement.close();
        connection.close();
    }

    @Override
    public String getSql() {
        return sql;
    }

}
