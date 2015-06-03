package com.github.terma.gigaspacesqlconsole;

import com.github.terma.gigaspacesqlconsole.core.ExecuteResponseStream;
import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

public class CsvExecuteResponseStreamTest {

    @Test
    public void shouldGenerateCorrectCsv() throws IOException {
        StringWriter writer = new StringWriter();

        ExecuteResponseStream responseStream = new CsvExecuteResponseStream(writer);
        responseStream.writeHeader(Arrays.asList("A", "COLUMN_B"));
        responseStream.writeRow(Arrays.asList("1", "2"));
        responseStream.writeRow(Arrays.asList("VALUE", "NAME"));

        Assert.assertEquals("A,COLUMN_B\r\n1,2\r\nVALUE,NAME\r\n", writer.toString());
    }

    @Test
    public void shouldGenerateCorrectCsvWithDoubleQuotasInText() throws IOException {
        StringWriter writer = new StringWriter();

        ExecuteResponseStream responseStream = new CsvExecuteResponseStream(writer);
        responseStream.writeHeader(Arrays.asList("A"));
        responseStream.writeRow(Arrays.asList("AR\"TE"));

        Assert.assertEquals("A\r\n\"AR\"\"TE\"\r\n", writer.toString());
    }

    @Test
    public void shouldGenerateCorrectCsvWithNewLineInText() throws IOException {
        StringWriter writer = new StringWriter();

        ExecuteResponseStream responseStream = new CsvExecuteResponseStream(writer);
        responseStream.writeHeader(Arrays.asList("A"));
        responseStream.writeRow(Arrays.asList("AR\nTE"));

        Assert.assertEquals("A\r\n\"AR\nTE\"\r\n", writer.toString());
    }

}
