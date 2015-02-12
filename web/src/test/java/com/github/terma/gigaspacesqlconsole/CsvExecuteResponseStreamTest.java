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

        Assert.assertEquals("A,COLUMN_B\n1,2\nVALUE,NAME\n", writer.toString());
    }

}
