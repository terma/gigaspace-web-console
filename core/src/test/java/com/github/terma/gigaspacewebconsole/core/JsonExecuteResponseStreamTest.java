package com.github.terma.gigaspacewebconsole.core;

import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

public class JsonExecuteResponseStreamTest {

    @Test
    public void shouldGenerateCorrectJson() throws IOException {
        StringWriter writer = new StringWriter();

        ExecuteResponseStream responseStream = new JsonExecuteResponseStream(writer);
        responseStream.writeHeader(Arrays.asList("A", "COLUMN_B"));
        responseStream.writeRow(Arrays.asList("1", "2"));
        responseStream.writeRow(Arrays.asList("VALUE", "NAME"));
        responseStream.close();

        Assert.assertEquals("{\"columns\":[\"A\",\"COLUMN_B\"],\"data\":[[\"1\",\"2\"],[\"VALUE\",\"NAME\"]]}", writer.toString());
    }

    @Test
    public void shouldEmptyJsonIfNoRows() throws IOException {
        StringWriter writer = new StringWriter();

        ExecuteResponseStream responseStream = new JsonExecuteResponseStream(writer);
        responseStream.writeHeader(Arrays.asList("A", "COLUMN_B"));
        responseStream.close();

        Assert.assertEquals("{\"columns\":[\"A\",\"COLUMN_B\"],\"data\":[]}", writer.toString());
    }

}
