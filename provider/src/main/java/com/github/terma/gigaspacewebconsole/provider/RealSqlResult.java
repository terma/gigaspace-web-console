/*
Copyright 2015 Artem Stasiuk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.github.terma.gigaspacewebconsole.provider;

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
    private final ConverterHelper converterHelper;

    public RealSqlResult(final Statement statement, final String sql,
                         final ConverterHelper converterHelper) throws SQLException {
        this.sql = sql;
        this.converterHelper = converterHelper;
        this.resultSet = statement.getResultSet();

        columns = new ArrayList<>();
        for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
            columns.add(resultSet.getMetaData().getColumnName(i));
        }
    }

    @SuppressWarnings("unused")
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
            row.add(converterHelper.getFormattedValue(resultSet, column));
        }
        return row;
    }

    @Override
    public List<String> getRowTypes() throws SQLException {
        List<String> types = new ArrayList<>();
        for (final String column : columns) {
            Object value = resultSet.getObject(column);
            if (value == null) types.add(null);
            else types.add(value.getClass().getName());
        }
        return types;
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
