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

package com.github.terma.gigaspacesqlconsole.provider;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class UpdateSqlParserTest {

    @Test
    public void parseUpdateWithoutConditions() throws IOException {
        UpdateSql sql = UpdateSqlParser.parse("update ControlData set A = '1'");

        Assert.assertEquals("ControlData", sql.typeName);
        Assert.assertEquals(1, sql.setFields.size());
        Assert.assertEquals("1", sql.setFields.get("A"));
    }

    @Test
    public void parseUpdateWithDotInTypeName() throws IOException {
        UpdateSql sql = UpdateSqlParser.parse("update com.ControlData set A = '1'");

        Assert.assertEquals("com.ControlData", sql.typeName);
        Assert.assertEquals(1, sql.setFields.size());
    }

    @Test
    public void parseUpdateWithUndescoreInTypeName() throws IOException {
        UpdateSql sql = UpdateSqlParser.parse("update com_ControlData set A = '1'");

        Assert.assertEquals("com_ControlData", sql.typeName);
        Assert.assertEquals(1, sql.setFields.size());
    }

    @Test
    public void parseUpdateWithDashInTypeName() throws IOException {
        UpdateSql sql = UpdateSqlParser.parse("update com-ControlData set A = '1'");

        Assert.assertEquals("com-ControlData", sql.typeName);
        Assert.assertEquals(1, sql.setFields.size());
    }

    @Test
    public void parseUpdateWithDashInFieldName() throws IOException {
        UpdateSql sql = UpdateSqlParser.parse("update ControlData set A-B = '1'");

        Assert.assertEquals("1", sql.setFields.get("A-B"));
    }

    @Test
    public void parseUpdateWithUndescoreInFieldName() throws IOException {
        UpdateSql sql = UpdateSqlParser.parse("update ControlData set A_B = '1'");

        Assert.assertEquals("1", sql.setFields.get("A_B"));
    }

    @Test
    public void parseUpdateWithDotInFieldName() throws IOException {
        UpdateSql sql = UpdateSqlParser.parse("update ControlData set A.B = '1'");

        Assert.assertEquals("1", sql.setFields.get("A.B"));
    }

    @Test
    public void parseUpdateWithSetToNull() throws IOException {
        UpdateSql sql = UpdateSqlParser.parse("update ControlData set A = null");

        Assert.assertEquals(null, sql.setFields.get("A"));
    }

    @Test
    public void parseAcceptRedundantSpaces() throws IOException {
        UpdateSql sql = UpdateSqlParser.parse("   update    ControlData    set   A    = '1'");

        Assert.assertEquals("ControlData", sql.typeName);
        Assert.assertEquals(1, sql.setFields.size());
        Assert.assertEquals("1", sql.setFields.get("A"));
    }

    @Test
    public void parseEmptyConditions() throws IOException {
        UpdateSql sql = UpdateSqlParser.parse("update ControlData set A ='1'");

        Assert.assertEquals("", sql.conditions);
    }

    @Test
    public void parseSetParameterWithSpecialSymbols() throws IOException {
        UpdateSql sql = UpdateSqlParser.parse("update ControlData set A ='-.!@#$%^&*()_+`;:[]{}/?<>'");

        Assert.assertEquals("-.!@#$%^&*()_+`;:[]{}/?<>", sql.setFields.get("A"));
    }

    @Test
    public void parseSetParameterWithSpecialSymbols1() throws IOException {
        UpdateSql sql = UpdateSqlParser.parse("update ControlData set A = 'A-A' where id = 1");

        Assert.assertEquals("A-A", sql.setFields.get("A"));
    }

    @Test
    public void parseSetParameterWithSpecialSymbolsMultiple() throws IOException {
        UpdateSql sql = UpdateSqlParser.parse(
                "update ControlData set A ='-.!@#$%^&*()_+`;:[]{}/?<>', B = true");

        Assert.assertEquals("-.!@#$%^&*()_+`;:[]{}/?<>", sql.setFields.get("A"));
        Assert.assertEquals(true, sql.setFields.get("B"));
    }

    @Test
    public void parseEmptyValue() throws IOException {
        UpdateSql sql = UpdateSqlParser.parse("update ControlData set A = ''");

        Assert.assertEquals(1, sql.setFields.size());
        Assert.assertEquals("", sql.setFields.get("A"));
    }

    @Test
    public void parseBooleanAsBoolean() throws IOException {
        UpdateSql sql = UpdateSqlParser.parse("update ControlData set A = true");

        Assert.assertEquals(1, sql.setFields.size());
        Assert.assertEquals(true, sql.setFields.get("A"));
    }

    @Test
    public void parseBooleanAsBooleanUpperCase() throws IOException {
        UpdateSql sql = UpdateSqlParser.parse("update ControlData set A = TRUE");

        Assert.assertEquals(1, sql.setFields.size());
        Assert.assertEquals(true, sql.setFields.get("A"));
    }

    @Test
    public void parseLongAsLong() throws IOException {
        UpdateSql sql = UpdateSqlParser.parse("update ControlData set A = 12");

        Assert.assertEquals(1, sql.setFields.size());
        Assert.assertEquals(12L, sql.setFields.get("A"));
    }

    @Test
    public void parseMultySetValues() throws IOException {
        UpdateSql sql = UpdateSqlParser.parse("update ControlData set N = '1', XYX = '90A'");

        Assert.assertEquals(2, sql.setFields.size());
        Assert.assertEquals("1", sql.setFields.get("N"));
        Assert.assertEquals("90A", sql.setFields.get("XYX"));
    }

    @Test
    public void parseConditions() throws IOException {
        UpdateSql sql = UpdateSqlParser.parse("update ControlData set A = '1' where MYM = NYN");

        Assert.assertEquals(" MYM = NYN", sql.conditions);
    }

    @Test(expected = IOException.class)
    public void throwExceptionWhenParseUpdateWithoutTypeName() throws IOException {
        UpdateSqlParser.parse("update");
    }

    @Test(expected = IOException.class)
    public void throwExceptionWhenNoAnySet() throws IOException {
        UpdateSqlParser.parse("update CD set");
    }

    @Test
    public void parseToNullIfNotUpdate() throws IOException {
        Assert.assertNull(UpdateSqlParser.parse("a"));
    }

    @Test(expected = IOException.class)
    public void throwExceptionWhenNotSet() throws IOException {
        UpdateSqlParser.parse("update F");
    }

}
