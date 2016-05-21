/*
Copyright 2015-2016 Artem Stasiuk

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

package com.github.terma.gigaspacewebconsole.provider.groovy;

import com.github.terma.gigaspacewebconsole.provider.SqlResult;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static java.util.Collections.singletonList;

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
        return singletonList("affected_rows");
    }

    public List<String> getRow() throws SQLException {
        return singletonList(Integer.toString(affectedRow));
    }

    @Override
    public List<String> getRowTypes() throws SQLException {
        return singletonList(Integer.class.getName());
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
