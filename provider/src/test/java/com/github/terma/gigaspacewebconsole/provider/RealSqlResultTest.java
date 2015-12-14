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

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import static org.mockito.Mockito.*;

public class RealSqlResultTest {

    private Statement statement;
    private ResultSet resultSet;
    private ResultSetMetaData resultSetMetaData;
    private ConverterHelper converterHelper;

    @Before
    public void init() throws SQLException {
        statement = mock(Statement.class);

        resultSet = mock(ResultSet.class);
        when(statement.getResultSet()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(false);

        resultSetMetaData = mock(ResultSetMetaData.class);
        when(resultSet.getMetaData()).thenReturn(resultSetMetaData);

        converterHelper = mock(ConverterHelper.class);
    }

    @Test
    public void shouldProvideRowTypesWithNullIfValuesNull() throws SQLException {
        when(resultSetMetaData.getColumnCount()).thenReturn(2);

        SqlResult sqlResult = new RealSqlResult(statement, "sql", converterHelper);

        Assert.assertTrue(sqlResult.next());
        Assert.assertEquals(Arrays.asList(null, null), sqlResult.getRowTypes());
    }

    @Test
    public void shouldProvideRowTypes() throws SQLException {
        when(resultSetMetaData.getColumnCount()).thenReturn(2);
        when(resultSet.getObject(null)).thenReturn("Boba").thenReturn(12.0d);

        SqlResult sqlResult = new RealSqlResult(statement, "sql", converterHelper);

        Assert.assertTrue(sqlResult.next());
        Assert.assertEquals(Arrays.asList("java.lang.String", "java.lang.Double"), sqlResult.getRowTypes());
    }

}
