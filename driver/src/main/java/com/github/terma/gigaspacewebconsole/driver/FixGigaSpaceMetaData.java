package com.github.terma.gigaspacewebconsole.driver;

import com.gigaspaces.internal.client.spaceproxy.ISpaceProxy;
import com.j_spaces.core.admin.JSpaceAdminProxy;
import com.j_spaces.jdbc.ResultEntry;
import com.j_spaces.jdbc.driver.GConnection;
import com.j_spaces.jdbc.driver.GDatabaseMetaData;
import com.j_spaces.jdbc.driver.GResultSet;

import java.lang.reflect.Field;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * ((JSpaceAdminProxy)gigaSpace.getSpace().getAdmin()).getRuntimeInfo().m_ClassNames
 */
public class FixGigaSpaceMetaData extends GDatabaseMetaData {

    private final GConnection connection;

    public FixGigaSpaceMetaData(GConnection connection) {
        super(connection);
        this.connection = connection;
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

        resultEntry.setFieldNames(new String[]{
                "TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "TABLE_TYPE"});

        List<String> tables = new ArrayList<>();
        try {
            Field spaceField = connection.getClass().getDeclaredField("space");
            spaceField.setAccessible(true);

            final ISpaceProxy spaceProxy = (ISpaceProxy) spaceField.get(connection);
            tables = ((JSpaceAdminProxy) spaceProxy.getAdmin()).getRuntimeInfo().m_ClassNames;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        Object[][] fieldValues = new Object[tables.size()][];
        resultEntry.setFieldValues(fieldValues);
        for (int i = 0; i < tables.size(); i++) {
            Object[] tableRow = new Object[]{null, null, tables.get(i), "TABLE"};
            fieldValues[i] = tableRow;
        }

        return new GResultSet(null, resultEntry);
    }

}
