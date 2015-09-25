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

/**
 * ((JSpaceAdminProxy)gigaSpace.getSpace().getAdmin()).getRuntimeInfo().m_ClassNames
 */
public class GoodMetaData extends GDatabaseMetaData {

    private final Connection connection;

    public GoodMetaData(Connection connection) {
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

    /**
     * Retrieves a description of the tables available in the given catalog.
     * Only table descriptions matching the catalog, schema, table
     * name and type criteria are returned.  They are ordered by
     * <code>TABLE_TYPE</code>, <code>TABLE_CAT</code>,
     * <code>TABLE_SCHEM</code> and <code>TABLE_NAME</code>.
     * <p/>
     * Each table description has the following columns:
     * <OL>
     * <LI><B>TABLE_CAT</B> String {@code =>} table catalog (may be <code>null</code>)
     * <LI><B>TABLE_SCHEM</B> String {@code =>} table schema (may be <code>null</code>)
     * <LI><B>TABLE_NAME</B> String {@code =>} table name
     * <LI><B>TABLE_TYPE</B> String {@code =>} table type.  Typical types are "TABLE",
     * "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY",
     * "LOCAL TEMPORARY", "ALIAS", "SYNONYM".
     * <LI><B>REMARKS</B> String {@code =>} explanatory comment on the table
     * <LI><B>TYPE_CAT</B> String {@code =>} the types catalog (may be <code>null</code>)
     * <LI><B>TYPE_SCHEM</B> String {@code =>} the types schema (may be <code>null</code>)
     * <LI><B>TYPE_NAME</B> String {@code =>} type name (may be <code>null</code>)
     * <LI><B>SELF_REFERENCING_COL_NAME</B> String {@code =>} name of the designated
     * "identifier" column of a typed table (may be <code>null</code>)
     * <LI><B>REF_GENERATION</B> String {@code =>} specifies how values in
     * SELF_REFERENCING_COL_NAME are created. Values are
     * "SYSTEM", "USER", "DERIVED". (may be <code>null</code>)
     * </OL>
     * <p/>
     * <P><B>Note:</B> Some databases may not return information for
     * all tables.
     *
     * @param catalog          a catalog name; must match the catalog name as it
     *                         is stored in the database; "" retrieves those without a catalog;
     *                         <code>null</code> means that the catalog name should not be used to narrow
     *                         the search
     * @param schemaPattern    a schema name pattern; must match the schema name
     *                         as it is stored in the database; "" retrieves those without a schema;
     *                         <code>null</code> means that the schema name should not be used to narrow
     *                         the search
     * @param tableNamePattern a table name pattern; must match the
     *                         table name as it is stored in the database
     * @param types            a list of table types, which must be from the list of table types
     *                         returned from {@link #getTableTypes},to include; <code>null</code> returns
     *                         all types
     * @return <code>ResultSet</code> - each row is a table description
     * @throws SQLException if a database access error occurs
     * @see #getSearchStringEscape
     */
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
