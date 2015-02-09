package com.github.terma.gigaspacesqlconsole.provider;

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

public class CopySqlParserTest {

    @Test
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
        assertEquals(new HashSet<>(Arrays.asList("fieldB")), copySql.reset);
        assertEquals("", copySql.where);
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
        assertEquals(new HashSet<>(Arrays.asList("field_C")), copySql.reset);
        assertEquals("", copySql.where);
    }

    @Test
    public void shouldAcceptCopyWithResetAndWhere() throws IOException {
        final CopySql copySql = CopySqlParser.parse("copy ObjectA reset fieldB, fieldA  where momo = 123");

        assertEquals("ObjectA", copySql.typeName);
        assertEquals(new HashSet<>(Arrays.asList("fieldB", "fieldA")), copySql.reset);
        assertEquals(" momo = 123", copySql.where);
    }

}
