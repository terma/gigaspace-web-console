package com.github.terma.gigaspacesqlconsole.provider.groovy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArraySqlResultBuilder {

    private final String sql;
    private final List<String> columns;
    private final List<List<String>> data;

    public ArraySqlResultBuilder(final String sql, final List<String> columns) {
        this.sql = sql;
        this.columns = columns;
        this.data = new ArrayList<>();
    }

    public ArraySqlResultBuilder add(String... rows) {
        data.add(Arrays.asList(rows));
        return this;
    }

    public ArraySqlResult createResult() {
        return new ArraySqlResult(sql, columns, data);
    }

}
