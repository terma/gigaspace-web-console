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

import java.sql.SQLException;
import java.util.Collections;
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
    public List<String> getRowTypes() throws SQLException {
        return Collections.emptyList();
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public String getSql() {
        return sql;
    }

}
