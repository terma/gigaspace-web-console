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

package com.github.terma.gigaspacesqlconsole.provider.executor;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class GenerateSqlParserTest {

    @Test
    public void parse() throws IOException {
        GenerateSql sql = GenerateSqlParser.parse("generate 1009 of Customer with A = '1', name = false");

        Assert.assertEquals(1009, sql.count);
        Assert.assertEquals("Customer", sql.typeName);
        Assert.assertEquals(2, sql.fields.size());
        Assert.assertEquals("1", sql.fields.get("A"));
        Assert.assertEquals(false, sql.fields.get("name"));
    }

    @Test
    public void parseCount() throws IOException {
        GenerateSql sql = GenerateSqlParser.parse("generate 1009 of com.Customer with A = '1'");

        Assert.assertEquals(1009, sql.count);
    }

    @Test(expected = IOException.class)
    public void throwExceptionWhenNoCount() throws IOException {
        GenerateSqlParser.parse("generate of com.Customer with A = '1'");
    }

    @Test(expected = IOException.class)
    public void throwExceptionWhenNonNumberCount() throws IOException {
        GenerateSqlParser.parse("generate AA of com.Customer with A = '1'");
    }

    @Test
    public void parseGenerateWithDotInTypeName() throws IOException {
        GenerateSql sql = GenerateSqlParser.parse("generate 1 of com.Customer with A = '1'");

        Assert.assertEquals("com.Customer", sql.typeName);
        Assert.assertEquals(1, sql.fields.size());
    }

    @Test
    public void parseGenerateWithUndescoreInTypeName() throws IOException {
        GenerateSql sql = GenerateSqlParser.parse("generate 1 of com_Customer with A = '1'");

        Assert.assertEquals("com_Customer", sql.typeName);
        Assert.assertEquals(1, sql.fields.size());
    }

    @Test
    public void parseGenerateWithDashInTypeName() throws IOException {
        GenerateSql sql = GenerateSqlParser.parse("generate 1 of com-Customer with A = '1'");

        Assert.assertEquals("com-Customer", sql.typeName);
        Assert.assertEquals(1, sql.fields.size());
    }

    @Test
    public void parseGenerateWithDashInFieldName() throws IOException {
        GenerateSql sql = GenerateSqlParser.parse("generate 1 of Customer with A-B = '1'");

        Assert.assertEquals("1", sql.fields.get("A-B"));
    }

    @Test
    public void parseGenerateWithUndescoreInFieldName() throws IOException {
        GenerateSql sql = GenerateSqlParser.parse("generate 20 of Customer with A_B = '1'");

        Assert.assertEquals("1", sql.fields.get("A_B"));
    }

    @Test
    public void parseGenerateWithDotInFieldName() throws IOException {
        GenerateSql sql = GenerateSqlParser.parse("generate 900 of Customer with A.B = '1'");

        Assert.assertEquals("1", sql.fields.get("A.B"));
    }

    @Test
    public void parseAcceptRedundantSpaces() throws IOException {
        GenerateSql sql = GenerateSqlParser.parse("   generate    1    of   Customer    with   A    = '1'");

        Assert.assertEquals("Customer", sql.typeName);
        Assert.assertEquals(1, sql.fields.size());
        Assert.assertEquals("1", sql.fields.get("A"));
    }

}
