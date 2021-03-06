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

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static junit.framework.Assert.*;

public class CopySqlParserTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNonCopy() throws IOException {
        assertNull(CopySqlParser.parse("select ObjectA"));
    }

    @Test(expected = IOException.class)
    public void shouldThrowExceptionIfWrongSync() throws IOException {
        CopySqlParser.parse("copy ObjectA pppp");
    }

    @Test
    public void shouldAcceptCopyWithoutWhereAndReset() throws IOException {
        final CopySql copySql = CopySqlParser.parse("copy ObjectA");

        assertEquals("ObjectA", copySql.typeName);
        assertTrue(copySql.reset.isEmpty());
        assertEquals("", copySql.where);
    }

    @Test
    public void shouldAcceptCopyWithDotInTypeName() throws IOException {
        final CopySql copySql = CopySqlParser.parse("copy com.ObjectA");

        assertEquals("com.ObjectA", copySql.typeName);
        assertTrue(copySql.reset.isEmpty());
        assertEquals("", copySql.where);
    }

    @Test
    public void shouldAcceptCopyWithUndescoreInTypeName() throws IOException {
        final CopySql copySql = CopySqlParser.parse("copy com_ObjectA");

        assertEquals("com_ObjectA", copySql.typeName);
        assertTrue(copySql.reset.isEmpty());
        assertEquals("", copySql.where);
    }

    @Test
    public void shouldAcceptCopyWithDashInTypeName() throws IOException {
        final CopySql copySql = CopySqlParser.parse("copy com-ObjectA");

        assertEquals("com-ObjectA", copySql.typeName);
        assertTrue(copySql.reset.isEmpty());
        assertEquals("", copySql.where);
    }

    @Test
    public void shouldAcceptCopyWithWhere() throws IOException {
        final CopySql copySql = CopySqlParser.parse("copy ObjectA where fieldName = 'ffwer' and fieldN=true");

        assertEquals("ObjectA", copySql.typeName);
        assertTrue(copySql.reset.isEmpty());
        assertEquals(" fieldName = 'ffwer' and fieldN=true", copySql.where);
    }

    @Test
    public void shouldAcceptCopyWithReset() throws IOException {
        final CopySql copySql = CopySqlParser.parse("copy ObjectA reset fieldB");

        assertEquals("ObjectA", copySql.typeName);
        assertEquals(new HashSet<>(Collections.singletonList("fieldB")), copySql.reset);
        assertEquals("", copySql.where);
    }

    @Test
    public void shouldAcceptCopyWithDashInResetField() throws IOException {
        final CopySql copySql = CopySqlParser.parse("copy ObjectA reset field-B");

        assertEquals(new HashSet<>(Collections.singletonList("field-B")), copySql.reset);
    }

    @Test
    public void shouldAcceptCopyWithDotInResetField() throws IOException {
        final CopySql copySql = CopySqlParser.parse("copy ObjectA reset field.B");

        assertEquals(new HashSet<>(Collections.singletonList("field.B")), copySql.reset);
    }

    @Test
    public void shouldAcceptCopyWithUndescoreInResetField() throws IOException {
        final CopySql copySql = CopySqlParser.parse("copy ObjectA reset field_B");

        assertEquals(new HashSet<>(Collections.singletonList("field_B")), copySql.reset);
    }

    @Test
    public void shouldAcceptCopyWithManyReset() throws IOException {
        final CopySql copySql = CopySqlParser.parse("copy ObjectA reset fieldB, fieldA ,   fieldC");

        assertEquals("ObjectA", copySql.typeName);
        assertEquals(new HashSet<>(Arrays.asList("fieldB", "fieldC", "fieldA")), copySql.reset);
        assertEquals("", copySql.where);
    }

    @Test
    public void shouldAcceptCopyWithTrickyResetFieldName() throws IOException {
        final CopySql copySql = CopySqlParser.parse("copy ObjectA reset field_C");

        assertEquals("ObjectA", copySql.typeName);
        assertEquals(new HashSet<>(Collections.singletonList("field_C")), copySql.reset);
        assertEquals("", copySql.where);
    }

    @Test
    public void supportFromOnly() throws IOException {
        final CopySql copySql = CopySqlParser.parse("copy ObjectA from 10 only 100");

        assertEquals("ObjectA", copySql.typeName);
        assertEquals("", copySql.where);
        assertEquals(new Integer(10), copySql.from);
        assertEquals(new Integer(100), copySql.only);
    }

    @Test
    public void supportFromWithoutOnly() throws IOException {
        final CopySql copySql = CopySqlParser.parse("copy ObjectA from 10");

        assertEquals("ObjectA", copySql.typeName);
        assertEquals("", copySql.where);
        assertEquals(new Integer(10), copySql.from);
        assertEquals(null, copySql.only);
    }

    @Test(expected = IOException.class)
    public void failsIfNonDigitFrom() throws IOException {
        CopySqlParser.parse("copy ObjectA from A");
    }

    @Test(expected = IOException.class)
    public void failsIfFromWithoutValue() throws IOException {
        CopySqlParser.parse("copy ObjectA from only 1");
    }

    @Test(expected = IOException.class)
    public void failsIfNonDigitOnly() throws IOException {
        CopySqlParser.parse("copy ObjectA to A");
    }

    @Test
    public void supportOnlyWithoutFrom() throws IOException {
        final CopySql copySql = CopySqlParser.parse("copy ObjectA only 100");

        assertEquals("ObjectA", copySql.typeName);
        assertEquals("", copySql.where);
        assertEquals(null, copySql.from);
        assertEquals(new Integer(100), copySql.only);
    }

    @Test
    public void shouldAcceptCopyWithResetAndWhere() throws IOException {
        final CopySql copySql = CopySqlParser.parse("copy ObjectA reset fieldB, fieldA  where momo = 123");

        assertEquals("ObjectA", copySql.typeName);
        assertEquals(new HashSet<>(Arrays.asList("fieldB", "fieldA")), copySql.reset);
        assertEquals(" momo = 123", copySql.where);
    }

}
