package com.github.terma.gigaspacesqlconsole.provider.groovy;

import java.sql.SQLException;
import java.util.List;

public class ArraySqlResult implements SqlResult {

    private final String sql;
    private final List<String> columns;
    private final List<List<String>> data;

    private int position = -1;

    public ArraySqlResult(final String sql, final List<String> columns, final List<List<String>> data) {
        this.sql = sql;
        this.columns = columns;
        this.data = data;
    }

    @Override
    public boolean next() throws SQLException {
        position++;
        return position < data.size();
    }

    @Override
    public List<String> getColumns() throws SQLException {
        return columns;
    }

    @Override
    public List<String> getRow() throws SQLException {
        return data.get(position);
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public String getSql() {
        return sql;
    }

}
