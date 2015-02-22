package com.github.terma.gigaspacesqlconsole.provider;

import com.github.terma.gigaspacesqlconsole.core.ExecuteRequest;
import com.github.terma.gigaspacesqlconsole.core.GroovyExecuteResponseStream;
import com.github.terma.gigaspacesqlconsole.provider.groovy.PrintClosure;
import com.github.terma.gigaspacesqlconsole.provider.groovy.SqlClosure;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public class GroovyExecutor {

    public static void execute(ExecuteRequest request, GroovyExecuteResponseStream responseStream) throws Exception {
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
