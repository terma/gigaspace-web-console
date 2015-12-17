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

import com.gigaspaces.internal.client.spaceproxy.ISpaceProxy;
import com.j_spaces.core.admin.JSpaceAdminProxy;
import com.j_spaces.jdbc.ResultEntry;
import com.j_spaces.jdbc.driver.GDatabaseMetaData;

import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class FixedMetaData extends GDatabaseMetaData {

    private final Connection connection;

    public FixedMetaData(Connection connection) {
        super(connection);
        this.connection = connection;
    }

    @Override
    public ResultSet getSchemas() {
        return new FixedResultSet();
    }

    @Override
    public ResultSet getCatalogs() {
        return new FixedResultSet();
    }

    @Override
    public ResultSet getProcedures(
            final String catalog, final String schemaPattern, final String procedureNamePattern) {
        return new FixedResultSet();
    }

    @Override
    public ResultSet getTableTypes() throws SQLException {
        final ResultEntry entry = new ResultEntry();
        entry.setFieldNames(new String[]{"TABLE_TYPE"});
        entry.setColumnLabels(entry.getFieldNames());

        entry.setFieldValues(new Object[][]{{"TABLE"}});
        return new FixedResultSet(null, entry);
    }

    @Override
    public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
        ResultEntry resultEntry = new ResultEntry();

        resultEntry.setColumnLabels(new String[]{
                "TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "TABLE_TYPE"});
        resultEntry.setFieldNames(resultEntry.getColumnLabels());

        List<String> tables;
        try {
            final ISpaceProxy spaceProxy = connection.getSpace();
            tables = ((JSpaceAdminProxy) spaceProxy.getAdmin()).getRuntimeInfo().m_ClassNames;
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        Object[][] fieldValues = new Object[tables.size()][];
        resultEntry.setFieldValues(fieldValues);
        for (int i = 0; i < tables.size(); i++) {
            Object[] tableRow = new Object[]{null, null, tables.get(i), "TABLE"};
            fieldValues[i] = tableRow;
        }

        return new FixedResultSet(null, resultEntry);
    }

}
