package com.github.terma.gigaspacewebconsole.server;

import com.github.terma.gigaspacewebconsole.core.ExecuteResponseStream;
import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class CsvExecuteResponseStreamTest {

    @Test
    public void shouldGenerateCorrectCsv() throws IOException {
        StringWriter writer = new StringWriter();

        ExecuteResponseStream responseStream = new CsvExecuteResponseStream(writer);
        responseStream.writeHeader(Arrays.asList("A", "COLUMN_B"));
        responseStream.writeRow(Arrays.asList("1", "2"), new ArrayList<String>());
        responseStream.writeRow(Arrays.asList("VALUE", "NAME"), new ArrayList<String>());

        Assert.assertEquals("A,COLUMN_B\r\n1,2\r\nVALUE,NAME\r\n", writer.toString());
    }

    @Test
    public void shouldGenerateCorrectCsvExcludeTypes() throws IOException {
        StringWriter writer = new StringWriter();

        ExecuteResponseStream responseStream = new CsvExecuteResponseStream(writer);
        responseStream.writeHeader(Arrays.asList("A", "COLUMN_B"));
        responseStream.writeRow(Arrays.asList("VALUE", "NAME"), Arrays.asList("String", "String"));

        Assert.assertEquals("A,COLUMN_B\r\nVALUE,NAME\r\n", writer.toString());
    }

    @Test
    public void shouldGenerateCorrectCsvWithDoubleQuotasInText() throws IOException {
        StringWriter writer = new StringWriter();

        ExecuteResponseStream responseStream = new CsvExecuteResponseStream(writer);
        responseStream.writeHeader(Arrays.asList("A"));
        responseStream.writeRow(Arrays.asList("AR\"TE"), new ArrayList<String>());

        Assert.assertEquals("A\r\n\"AR\"\"TE\"\r\n", writer.toString());
    }

    @Test
    public void shouldGenerateCorrectCsvWithNewLineInText() throws IOException {
        StringWriter writer = new StringWriter();

        ExecuteResponseStream responseStream = new CsvExecuteResponseStream(writer);
        responseStream.writeHeader(Arrays.asList("A"));
        responseStream.writeRow(Arrays.asList("AR\nTE"), new ArrayList<String>());

        Assert.assertEquals("A\r\n\"AR\nTE\"\r\n", writer.toString());
    }

}
