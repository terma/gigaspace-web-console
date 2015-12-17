package com.github.terma.gigaspacewebconsole.server;

import com.github.terma.gigaspacewebconsole.core.GroovyExecuteResponseStream;
import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

public class JsonGroovyExecuteResponseStreamTest {

    @Test
    public void shouldGenerateCorrectJsonIfNoResult() throws IOException {
        StringWriter writer = new StringWriter();

        GroovyExecuteResponseStream responseStream = new JsonGroovyExecuteResponseStream(writer);
        responseStream.close();

        Assert.assertEquals("[]", writer.toString());
    }

    @Test
    public void shouldGenerateCorrectJsonIfNoRows() throws IOException {
        StringWriter writer = new StringWriter();

        GroovyExecuteResponseStream responseStream = new JsonGroovyExecuteResponseStream(writer);
        responseStream.startResult("SQL1");
        responseStream.writeColumns(Arrays.asList("A", "B"));
        responseStream.closeResult();
        responseStream.close();

        Assert.assertEquals("[{\"header\":\"SQL1\",\"columns\":[\"A\",\"B\"],\"data\":[]}]", writer.toString());
    }

    @Test
    public void shouldGenerateCorrectJson() throws IOException {
        StringWriter writer = new StringWriter();

        GroovyExecuteResponseStream responseStream = new JsonGroovyExecuteResponseStream(writer);
        responseStream.startResult("SQL1");
        responseStream.writeColumns(Arrays.asList("A", "B"));
        responseStream.writeRow(Arrays.asList("1", "2"));
        responseStream.writeRow(Arrays.asList("NN", "MM"));
        responseStream.closeResult();
        responseStream.close();

        Assert.assertEquals("[{\"header\":\"SQL1\",\"columns\":[\"A\",\"B\"],\"data\":[[\"1\",\"2\"],[\"NN\",\"MM\"]]}]", writer.toString());
    }

}
