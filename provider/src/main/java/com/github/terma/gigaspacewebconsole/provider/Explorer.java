/*
Copyright 2015-2017 Artem Stasiuk
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

import com.github.terma.gigaspacewebconsole.core.ExploreRequest;
import com.github.terma.gigaspacewebconsole.core.ExploreResponse;
import com.github.terma.gigaspacewebconsole.core.ExploreTable;
import com.github.terma.gigaspacewebconsole.provider.executor.ConnectionFactory;
import com.github.terma.gigaspacewebconsole.provider.executor.gigaspace.GigaSpaceConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Explorer {

    private static final ConnectionFactory CONNECTION_FACTORY = new GigaSpaceConnectionFactory();

    public static ExploreResponse explore(final ExploreRequest request) throws Exception {
        if (request == null) throw new NullPointerException("Null request!");

        ExploreResponse response = new ExploreResponse();
        response.tables = new ArrayList<>();

        try (final Connection connection = CONNECTION_FACTORY.get(request)) {
            try (final ResultSet resultSet = connection.getMetaData().getTables(null, null, null, null)) {
                while (resultSet.next()) {
                    final String tableName = resultSet.getString("TABLE_NAME");

                    if (!tableName.contains(" ")) {
                        final List<String> columns = new ArrayList<>();

                        collectFixedColumns(connection, tableName, columns);
                        collectDynamicColumns(connection, tableName, columns);

                        response.tables.add(new ExploreTable(tableName, columns));
                    }
                }
            }
        }

        return response;
    }

    private static void collectFixedColumns(Connection connection, String tableName, List<String> columns) throws SQLException {
        try (final ResultSet columnsResultSet = connection.getMetaData().getColumns(null, null, tableName, null)) {
            while (columnsResultSet.next()) {
                columns.add(columnsResultSet.getString("COLUMN_NAME"));
            }
        }
    }

    private static void collectDynamicColumns(Connection connection, String tableName, List<String> columns) throws SQLException {
        try (final PreparedStatement ps = connection.prepareStatement("select * from " + tableName + " where rownum < 2")) {
            try (final ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        String columnName = rs.getMetaData().getColumnName(i);
                        if (!columns.contains(columnName)) columns.add(columnName);
                    }
                }
            }
        }
    }

}
