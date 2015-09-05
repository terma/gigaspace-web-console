package com.github.terma.gigaspacewebconsole.core;

import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

public class ObjectExecuteResponseStreamTest {

    @Test
    public void shouldCatchAllData() throws IOException {
        List<String> header = Arrays.asList("A", "COLUMN_B");
        List<String> row1 = Arrays.asList("1", "2");
        List<String> row2 = Arrays.asList("VALUE", "NAME");

        ObjectExecuteResponseStream responseStream = new ObjectExecuteResponseStream();
        responseStream.writeHeader(header);
        responseStream.writeRow(row1);
        responseStream.writeRow(row2);
        responseStream.close();

        Assert.assertEquals(header, responseStream.getColumns());
        Assert.assertEquals(Arrays.asList(row1, row2), responseStream.getData());
    }

    @Test
    public void shouldBeEmptyIfNoRows() throws IOException {
        ObjectExecuteResponseStream responseStream = new ObjectExecuteResponseStream();
        responseStream.writeHeader(Arrays.asList("A", "COLUMN_B"));
        responseStream.close();

        Assert.assertEquals(0, responseStream.getData().size());
    }

}
