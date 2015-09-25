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

import junit.framework.Assert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.openspaces.core.GigaSpace;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class GoodMetaDataTest {

    private Connection connection;
    private DatabaseMetaData metaData;

    @Before
    public void before() throws SQLException, ClassNotFoundException {
        connection = new Connection(Driver.JDBC_PREFIX + "/./meta-data");
        metaData = new GoodMetaData(connection);

    }

    @Test
    public void shouldGetOnlyObjectTypeIfNoRegisteredTypes() throws SQLException {
        ResultSet tables = metaData.getTables(null, null, null, null);
        assertThat(tables.next(), equalTo(true));
        assertThat(tables.getString(1), equalTo(null));
        assertThat(tables.getString(2), equalTo(null));
        assertThat(tables.getString(3), equalTo("java.lang.Object"));
    }

    @Test
    public void returnCorrectColumnsForTables() throws SQLException {
        ResultSet tables = metaData.getTables(null, null, null, null);

        tables.next();
        Assert.assertEquals(null, tables.getString("TABLE_CAT"));
        Assert.assertEquals(null, tables.getString("TABLE_SCHEM"));
        Assert.assertEquals("java.lang.Object", tables.getString("TABLE_NAME"));
        Assert.assertEquals("TABLE", tables.getString("TABLE_TYPE"));
    }

    @Test
    public void shouldGetTableForEachRegisteredType() throws SQLException, ClassNotFoundException {
        GigaSpace gigaSpace = GigaSpaceUtils.getGigaSpace("/./meta-data-non-empty");

        GigaSpaceUtils.registerType(gigaSpace, "A");
        GigaSpaceUtils.registerType(gigaSpace, "Dida");

        Connection connection = new Connection(Driver.JDBC_PREFIX + "/./meta-data-non-empty");
        ResultSet tables = new GoodMetaData(connection).getTables(null, null, null, null);
        assertThat(tables.next(), equalTo(true));
        assertThat(tables.getString(1), equalTo(null));
        assertThat(tables.getString(2), equalTo(null));
        assertThat(tables.getString(3), equalTo("java.lang.Object"));

        assertThat(tables.next(), equalTo(true));
        assertThat(tables.getString(1), equalTo(null));
        assertThat(tables.getString(2), equalTo(null));
        assertThat(tables.getString(3), equalTo("A"));

        assertThat(tables.next(), equalTo(true));
        assertThat(tables.getString(1), equalTo(null));
        assertThat(tables.getString(2), equalTo(null));
        assertThat(tables.getString(3), equalTo("Dida"));
    }

    @Test
    public void emptyResultSetMetaDataForSchemas() throws SQLException {
        assertEqualsResultSet(new FixedResultSet(), metaData.getSchemas());
    }

    @Test
    public void emptyResultSetMetaDataForCatalogs() throws SQLException {
        assertEqualsResultSet(new FixedResultSet(), metaData.getCatalogs());
    }

    @Test
    public void emptyResultSetMetaDataForProcedures() throws SQLException {
        assertEqualsResultSet(new FixedResultSet(), metaData.getProcedures(null, null, null));
    }

    @Test
    public void correctResultSetForTableTypes() throws SQLException {
        assertValidResultSet(metaData.getTableTypes());
    }

    private void assertValidResultSet(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();

        Assert.assertNotNull(metaData);
        assertThat(metaData.getColumnCount(), Matchers.greaterThanOrEqualTo(0));

        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            metaData.getCatalogName(i);
            metaData.getColumnClassName(i);
            metaData.getColumnLabel(i);
            metaData.getColumnName(i);
            metaData.getColumnType(i);
            metaData.getColumnTypeName(i);
        }

        if (resultSet.next()) {
            assertThat(metaData.getColumnCount(), Matchers.greaterThanOrEqualTo(1));
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                resultSet.getObject(i);
            }
        }
    }

    private void assertEqualsResultSet(ResultSet expected, ResultSet actual) throws SQLException {
        assertEqualsResultSetMetaData(expected.getMetaData(), actual.getMetaData());

        List<List<Object>> expectedTable = resultSetToTable(expected);
        List<List<Object>> actualTable = resultSetToTable(actual);

        Assert.assertEquals(expectedTable, actualTable);
    }

    private void assertEqualsResultSetMetaData(ResultSetMetaData expected, ResultSetMetaData actual) throws SQLException {
        Assert.assertEquals(expected.getColumnCount(), actual.getColumnCount());
    }

    private List<List<Object>> resultSetToTable(ResultSet resultSet) throws SQLException {
        List<List<Object>> result = new ArrayList<>();
        while (resultSet.next()) {
            List<Object> row = new ArrayList<>();
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                row.add(resultSet.getObject(i));
            }
            result.add(row);
        }
        return result;
    }

}
