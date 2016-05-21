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

package com.github.terma.gigaspacewebconsole.provider.driver;

import junit.framework.Assert;
import org.junit.Test;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class EmptyResultSetMetaDataTest {

    private final ResultSetMetaData resultSetMetaData = new EmptyResultSetMetaData();

    @Test
    public void zeroColumnCount() throws SQLException {
        Assert.assertEquals(0, resultSetMetaData.getColumnCount());
    }

    @Test
    public void isAutoIncrementAlwaysFalse() throws SQLException {
        Assert.assertFalse(resultSetMetaData.isAutoIncrement(100));
        Assert.assertFalse(resultSetMetaData.isAutoIncrement(1));
        Assert.assertFalse(resultSetMetaData.isAutoIncrement(0));
        Assert.assertFalse(resultSetMetaData.isAutoIncrement(-1));
    }

    @Test
    public void isCaseSensitiveAlwaysFalse() throws SQLException {
        Assert.assertFalse(resultSetMetaData.isCaseSensitive(100));
        Assert.assertFalse(resultSetMetaData.isCaseSensitive(1));
        Assert.assertFalse(resultSetMetaData.isCaseSensitive(0));
        Assert.assertFalse(resultSetMetaData.isCaseSensitive(-1));
    }

    @Test
    public void isSearchableAlwaysFalse() throws SQLException {
        Assert.assertFalse(resultSetMetaData.isSearchable(100));
        Assert.assertFalse(resultSetMetaData.isSearchable(1));
        Assert.assertFalse(resultSetMetaData.isSearchable(0));
        Assert.assertFalse(resultSetMetaData.isSearchable(-1));
    }

    @Test
    public void isCurrencyAlwaysFalse() throws SQLException {
        Assert.assertFalse(resultSetMetaData.isCurrency(100));
        Assert.assertFalse(resultSetMetaData.isCurrency(1));
        Assert.assertFalse(resultSetMetaData.isCurrency(0));
        Assert.assertFalse(resultSetMetaData.isCurrency(-1));
    }

    @Test
    public void isNullableAlwaysZero() throws SQLException {
        Assert.assertEquals(0, resultSetMetaData.isNullable(100));
        Assert.assertEquals(0, resultSetMetaData.isNullable(1));
        Assert.assertEquals(0, resultSetMetaData.isNullable(0));
        Assert.assertEquals(0, resultSetMetaData.isNullable(-1));
    }

    @Test
    public void getColumnDisplaySizeAlwaysZero() throws SQLException {
        Assert.assertEquals(0, resultSetMetaData.getColumnDisplaySize(100));
        Assert.assertEquals(0, resultSetMetaData.getColumnDisplaySize(1));
        Assert.assertEquals(0, resultSetMetaData.getColumnDisplaySize(0));
        Assert.assertEquals(0, resultSetMetaData.getColumnDisplaySize(-1));
    }

    @Test
    public void getPrecisionAlwaysZero() throws SQLException {
        Assert.assertEquals(0, resultSetMetaData.getPrecision(100));
        Assert.assertEquals(0, resultSetMetaData.getPrecision(1));
        Assert.assertEquals(0, resultSetMetaData.getPrecision(0));
        Assert.assertEquals(0, resultSetMetaData.getPrecision(-1));
    }

    @Test
    public void getScaleAlwaysZero() throws SQLException {
        Assert.assertEquals(0, resultSetMetaData.getScale(100));
        Assert.assertEquals(0, resultSetMetaData.getScale(1));
        Assert.assertEquals(0, resultSetMetaData.getScale(0));
        Assert.assertEquals(0, resultSetMetaData.getScale(-1));
    }

    @Test
    public void getColumnTypeAlwaysZero() throws SQLException {
        Assert.assertEquals(0, resultSetMetaData.getColumnType(100));
        Assert.assertEquals(0, resultSetMetaData.getColumnType(1));
        Assert.assertEquals(0, resultSetMetaData.getColumnType(0));
        Assert.assertEquals(0, resultSetMetaData.getColumnType(-1));
    }

    @Test
    public void isSignedAlwaysFalse() throws SQLException {
        Assert.assertFalse(resultSetMetaData.isSigned(100));
        Assert.assertFalse(resultSetMetaData.isSigned(1));
        Assert.assertFalse(resultSetMetaData.isSigned(0));
        Assert.assertFalse(resultSetMetaData.isSigned(-1));
    }

    @Test
    public void isReadOnlyAlwaysFalse() throws SQLException {
        Assert.assertFalse(resultSetMetaData.isReadOnly(100));
        Assert.assertFalse(resultSetMetaData.isReadOnly(1));
        Assert.assertFalse(resultSetMetaData.isReadOnly(0));
        Assert.assertFalse(resultSetMetaData.isReadOnly(-1));
    }

    @Test
    public void isDefinitelyWritableAlwaysFalse() throws SQLException {
        Assert.assertFalse(resultSetMetaData.isDefinitelyWritable(100));
        Assert.assertFalse(resultSetMetaData.isDefinitelyWritable(1));
        Assert.assertFalse(resultSetMetaData.isDefinitelyWritable(0));
        Assert.assertFalse(resultSetMetaData.isDefinitelyWritable(-1));
    }

    @Test
    public void isWritableAlwaysFalse() throws SQLException {
        Assert.assertFalse(resultSetMetaData.isWritable(100));
        Assert.assertFalse(resultSetMetaData.isWritable(1));
        Assert.assertFalse(resultSetMetaData.isWritable(0));
        Assert.assertFalse(resultSetMetaData.isWritable(-1));
    }

    @Test
    public void getColumnLabelAlwaysNull() throws SQLException {
        Assert.assertNull(resultSetMetaData.getColumnLabel(100));
        Assert.assertNull(resultSetMetaData.getColumnLabel(1));
        Assert.assertNull(resultSetMetaData.getColumnLabel(0));
        Assert.assertNull(resultSetMetaData.getColumnLabel(-1));
    }

    @Test
    public void getColumnNameAlwaysNull() throws SQLException {
        Assert.assertNull(resultSetMetaData.getColumnName(100));
        Assert.assertNull(resultSetMetaData.getColumnName(1));
        Assert.assertNull(resultSetMetaData.getColumnName(0));
        Assert.assertNull(resultSetMetaData.getColumnName(-1));
    }

    @Test
    public void getSchemaNameAlwaysNull() throws SQLException {
        Assert.assertNull(resultSetMetaData.getSchemaName(100));
        Assert.assertNull(resultSetMetaData.getSchemaName(1));
        Assert.assertNull(resultSetMetaData.getSchemaName(0));
        Assert.assertNull(resultSetMetaData.getSchemaName(-1));
    }

    @Test
    public void getTableNameAlwaysNull() throws SQLException {
        Assert.assertNull(resultSetMetaData.getTableName(100));
        Assert.assertNull(resultSetMetaData.getTableName(1));
        Assert.assertNull(resultSetMetaData.getTableName(0));
        Assert.assertNull(resultSetMetaData.getTableName(-1));
    }

    @Test
    public void getCatalogNameAlwaysNull() throws SQLException {
        Assert.assertNull(resultSetMetaData.getCatalogName(100));
        Assert.assertNull(resultSetMetaData.getCatalogName(1));
        Assert.assertNull(resultSetMetaData.getCatalogName(0));
        Assert.assertNull(resultSetMetaData.getCatalogName(-1));
    }

    @Test
    public void getColumnTypeNameAlwaysNull() throws SQLException {
        Assert.assertNull(resultSetMetaData.getColumnTypeName(100));
        Assert.assertNull(resultSetMetaData.getColumnTypeName(1));
        Assert.assertNull(resultSetMetaData.getColumnTypeName(0));
        Assert.assertNull(resultSetMetaData.getColumnTypeName(-1));
    }

    @Test
    public void getColumnClassNameAlwaysNull() throws SQLException {
        Assert.assertNull(resultSetMetaData.getColumnClassName(100));
        Assert.assertNull(resultSetMetaData.getColumnClassName(1));
        Assert.assertNull(resultSetMetaData.getColumnClassName(0));
        Assert.assertNull(resultSetMetaData.getColumnClassName(-1));
    }

    @Test
    public void unwrapAnyToNull() throws SQLException {
        Assert.assertNull(resultSetMetaData.unwrap(String.class));
        Assert.assertNull(resultSetMetaData.unwrap(Integer.class));
        Assert.assertNull(resultSetMetaData.unwrap(null));
    }

    @Test
    public void isWrappedForAlwaysFalse() throws SQLException {
        Assert.assertFalse(resultSetMetaData.isWrapperFor(String.class));
        Assert.assertFalse(resultSetMetaData.isWrapperFor(Integer.class));
        Assert.assertFalse(resultSetMetaData.isWrapperFor(null));
    }

}
