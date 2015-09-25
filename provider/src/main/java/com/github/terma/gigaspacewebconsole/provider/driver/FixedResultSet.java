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

package com.github.terma.gigaspacewebconsole.provider.driver;

import com.j_spaces.jdbc.ResultEntry;
import com.j_spaces.jdbc.driver.GResultSet;
import com.j_spaces.jdbc.driver.GResultSetMetaData;
import com.j_spaces.jdbc.driver.GStatement;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class FixedResultSet extends GResultSet {

    private final ResultEntry results;

    public FixedResultSet(final GStatement statement, final ResultEntry results) {
        super(statement, results);
        this.results = results;
    }

    public FixedResultSet() {
        this(null, null);
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        if (results == null) return new EmptyResultSetMetaData();
        else return new GResultSetMetaData(this.results);
    }

}
