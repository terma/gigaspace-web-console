package com.github.terma.gigaspacesqlconsole.provider.groovy;

import groovy.lang.Closure;

import java.util.Arrays;

public class PrintClosure extends Closure {

    private final GroovyExecuteResponseStream responseStream;

    public PrintClosure(final GroovyExecuteResponseStream responseStream) {
        super(null);
        this.responseStream = responseStream;
    }

    @Override
    public Object call(Object... args) {
        for (Object arg : args) print(arg, responseStream);
        return null;
    }

    @Override
    public Object call(Object arguments) {
        print(arguments, responseStream);
        return null;
    }

    private static void print(Object value, GroovyExecuteResponseStream responseStream) {
        responseStream.startResult("result");
        responseStream.writeColumns(Arrays.asList(""));
        responseStream.writeRow(Arrays.asList(value.toString()));
        responseStream.closeResult();
    }

}
