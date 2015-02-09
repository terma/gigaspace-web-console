package com.github.terma.gigaspacesqlconsole.provider;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class GigaSpaceUpdateSqlParserTest {

    @Test
    public void parseUpdateWithoutConditions() throws IOException {
        GigaSpaceUpdateSql sql = GigaSpaceUpdateSqlParser.parse("update ControlData set A = '1'");

        Assert.assertEquals("ControlData", sql.typeName);
        Assert.assertEquals(1, sql.setFields.size());
        Assert.assertEquals("1", sql.setFields.get("A"));
    }

    @Test
    public void parseAcceptRedundantSpaces() throws IOException {
        GigaSpaceUpdateSql sql = GigaSpaceUpdateSqlParser.parse("   update    ControlData    set   A    = '1'");

        Assert.assertEquals("ControlData", sql.typeName);
        Assert.assertEquals(1, sql.setFields.size());
        Assert.assertEquals("1", sql.setFields.get("A"));
    }

    @Test
    public void parseEmptyConditions() throws IOException {
        GigaSpaceUpdateSql sql = GigaSpaceUpdateSqlParser.parse("update ControlData set A ='1'");

        Assert.assertEquals("", sql.conditions);
    }

    @Test
    public void parseSetParameterWithSpecialSymbols() throws IOException {
        GigaSpaceUpdateSql sql = GigaSpaceUpdateSqlParser.parse("update ControlData set A ='-.!@#$%^&*()_+`;:[]{}/?<>'");

        Assert.assertEquals("-.!@#$%^&*()_+`;:[]{}/?<>", sql.setFields.get("A"));
    }

    @Test
    public void parseSetParameterWithSpecialSymbols1() throws IOException {
        GigaSpaceUpdateSql sql = GigaSpaceUpdateSqlParser.parse("update ControlData set A = 'A-A' where id = 1");

        Assert.assertEquals("A-A", sql.setFields.get("A"));
    }

    @Test
    public void parseSetParameterWithSpecialSymbolsMultiple() throws IOException {
        GigaSpaceUpdateSql sql = GigaSpaceUpdateSqlParser.parse(
                "update ControlData set A ='-.!@#$%^&*()_+`;:[]{}/?<>', B = true");

        Assert.assertEquals("-.!@#$%^&*()_+`;:[]{}/?<>", sql.setFields.get("A"));
        Assert.assertEquals(true, sql.setFields.get("B"));
    }

    @Test
    public void parseEmptyValue() throws IOException {
        GigaSpaceUpdateSql sql = GigaSpaceUpdateSqlParser.parse("update ControlData set A = ''");

        Assert.assertEquals(1, sql.setFields.size());
        Assert.assertEquals("", sql.setFields.get("A"));
    }

    @Test
    public void parseBooleanAsBoolean() throws IOException {
        GigaSpaceUpdateSql sql = GigaSpaceUpdateSqlParser.parse("update ControlData set A = true");

        Assert.assertEquals(1, sql.setFields.size());
        Assert.assertEquals(true, sql.setFields.get("A"));
    }

    @Test
    public void parseBooleanAsBooleanUpperCase() throws IOException {
        GigaSpaceUpdateSql sql = GigaSpaceUpdateSqlParser.parse("update ControlData set A = TRUE");

        Assert.assertEquals(1, sql.setFields.size());
        Assert.assertEquals(true, sql.setFields.get("A"));
    }

    @Test
    public void parseLongAsLong() throws IOException {
        GigaSpaceUpdateSql sql = GigaSpaceUpdateSqlParser.parse("update ControlData set A = 12");

        Assert.assertEquals(1, sql.setFields.size());
        Assert.assertEquals(12L, sql.setFields.get("A"));
    }

    @Test
    public void parseMultySetValues() throws IOException {
        GigaSpaceUpdateSql sql = GigaSpaceUpdateSqlParser.parse("update ControlData set N = '1', XYX = '90A'");

        Assert.assertEquals(2, sql.setFields.size());
        Assert.assertEquals("1", sql.setFields.get("N"));
        Assert.assertEquals("90A", sql.setFields.get("XYX"));
    }

    @Test
    public void parseConditions() throws IOException {
        GigaSpaceUpdateSql sql = GigaSpaceUpdateSqlParser.parse("update ControlData set A = '1' where MYM = NYN");

        Assert.assertEquals(" MYM = NYN", sql.conditions);
    }

    @Test(expected = IOException.class)
    public void throwExceptionWhenParseUpdateWithoutTypeName() throws IOException {
        GigaSpaceUpdateSqlParser.parse("update");
    }

    @Test(expected = IOException.class)
    public void throwExceptionWhenNoAnySet() throws IOException {
        GigaSpaceUpdateSqlParser.parse("update CD set");
    }

    @Test
    public void parseToNullIfNotUpdate() throws IOException {
        Assert.assertNull(GigaSpaceUpdateSqlParser.parse("a"));
    }

    @Test(expected = IOException.class)
    public void throwExceptionWhenNotSet() throws IOException {
        GigaSpaceUpdateSqlParser.parse("update F");
    }

}
