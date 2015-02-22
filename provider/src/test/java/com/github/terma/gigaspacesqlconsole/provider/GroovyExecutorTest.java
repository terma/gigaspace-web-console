package com.github.terma.gigaspacesqlconsole.provider;

import com.github.terma.gigaspacesqlconsole.core.ExecuteRequest;
import com.github.terma.gigaspacesqlconsole.provider.groovy.GroovyExecuteResponseStream;
import com.github.terma.gigaspacesqlconsole.provider.groovy.ObjectGroovyExecuteResponseStream;
import com.github.terma.gigaspacesqlconsole.provider.groovy.PrintClosure;
import com.github.terma.gigaspacesqlconsole.provider.groovy.SqlClosure;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
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

        execute(request, responseStream);
    }

    @Test
    public void shouldExecuteMultiline() throws Exception {
        request.sql = "1+1\n1+2";

        execute(request, responseStream);
    }

    @Test
    public void shouldPrintLastResultByDefault() throws Exception {
        request.sql = "\"fff\"";

        execute(request, responseStream);

        assertEquals(1, responseStream.results.size());
        assertEquals("fff", responseStream.results.get(0).data.get(0).get(0));
    }

    @Test
    public void shouldNotPrintByDefaultIfLastNotResult() throws Exception {
        request.sql = "";

        execute(request, responseStream);

        assertEquals(0, responseStream.results.size());
    }

    @Test(expected = Exception.class)
    public void shouldHandleException() throws Exception {
        request.sql = "1/0";

        execute(request, responseStream);
    }

    @Test
    public void shouldAllowPrintFromScript() throws Exception {
        request.sql = "out 12";

        execute(request, responseStream);

        assertEquals(1, responseStream.results.size());
    }

    @Test
    public void shouldAllowPrintMultiFromScript() throws Exception {
        request.sql = "out 12, 'aaa'";

        execute(request, responseStream);

        assertEquals(2, responseStream.results.size());
    }

    @Test
    public void shouldAllowToUseGsInstanceInScript() throws Exception {
        request.sql = "gs.clear(null)";

        execute(request, responseStream);
    }

    @Test
    public void shouldAllowToExecuteSql() throws Exception {
        request.sql = "sql 'create table Customer (id int)'";

        execute(request, responseStream);

        assertEquals(1, responseStream.results.size());
    }

    private static void execute(ExecuteRequest request, GroovyExecuteResponseStream responseStream) throws Exception {
        final Binding binding = new Binding();

        binding.setVariable("gs", GigaSpaceUtils.getGigaSpace(request));

        binding.setVariable("sql", new SqlClosure(request));

        final PrintClosure printClosure = new PrintClosure(responseStream);
        binding.setVariable("out", printClosure);

        final GroovyShell shell = new GroovyShell(binding);

        final Object value = shell.evaluate(request.sql);
        if (value != null) printClosure.call(value);
        responseStream.close();
    }

}
