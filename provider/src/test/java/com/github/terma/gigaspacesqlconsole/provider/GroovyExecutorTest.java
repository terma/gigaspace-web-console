package com.github.terma.gigaspacesqlconsole.provider;

import com.github.terma.gigaspacesqlconsole.core.ExecuteRequest;
import com.github.terma.gigaspacesqlconsole.provider.groovy.ObjectGroovyExecuteResponseStream;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class GroovyExecutorTest {

    private ObjectGroovyExecuteResponseStream responseStream = new ObjectGroovyExecuteResponseStream();
    private ExecuteRequest request = new ExecuteRequest();

    @Before
    public void before() {
        request.url = "/./" + this.getClass().getName();
    }

    @Test
    public void shouldExecuteSimpleGroovy() throws Exception {
        request.sql = "1+1";

        GroovyExecutor.execute(request, responseStream);
    }

    @Test
    public void shouldExecuteMultiline() throws Exception {
        request.sql = "1+1\n1+2";

        GroovyExecutor.execute(request, responseStream);
    }

    @Test
    public void shouldPrintLastResultByDefault() throws Exception {
        request.sql = "\"fff\"";

        GroovyExecutor.execute(request, responseStream);

        assertEquals(1, responseStream.results.size());
        assertEquals("fff", responseStream.results.get(0).data.get(0).get(0));
    }

    @Test
    public void shouldNotPrintByDefaultIfLastNotResult() throws Exception {
        request.sql = "";

        GroovyExecutor.execute(request, responseStream);

        assertEquals(0, responseStream.results.size());
    }

    @Test(expected = Exception.class)
    public void shouldHandleException() throws Exception {
        request.sql = "1/0";

        GroovyExecutor.execute(request, responseStream);
    }

    @Test
    public void shouldAllowPrintFromScript() throws Exception {
        request.sql = "out 12";

        GroovyExecutor.execute(request, responseStream);

        assertEquals(1, responseStream.results.size());
    }

    @Test
    public void shouldAllowPrintMultiFromScript() throws Exception {
        request.sql = "out 12, 'aaa'";

        GroovyExecutor.execute(request, responseStream);

        assertEquals(2, responseStream.results.size());
    }

    @Test
    public void shouldAllowToUseGsInstanceInScript() throws Exception {
        request.sql = "gs.clear(null)";

        GroovyExecutor.execute(request, responseStream);
    }

    @Test
    public void shouldAllowToExecuteSql() throws Exception {
        request.sql = "sql 'create table Customer (id int)'";

        GroovyExecutor.execute(request, responseStream);


        assertEquals(1, responseStream.results.size());
    }
    @Test
    public void shouldAllowToGetSpaceMemUsage() throws Exception {
        request.sql = "mem()";

        GroovyExecutor.execute(request, responseStream);

        assertEquals(1, responseStream.results.size());
    }

}
