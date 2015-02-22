package com.github.terma.gigaspacesqlconsole.provider.groovy;

import com.github.terma.gigaspacesqlconsole.core.GroovyExecuteResponseStream;
import groovy.lang.Closure;

import java.io.IOException;
import java.sql.SQLException;
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

    private static void print(final Object value, final GroovyExecuteResponseStream responseStream) {
        try {
            if (value instanceof SqlResult) {
                final SqlResult sqlResult = (SqlResult) value;
                responseStream.startResult(sqlResult.getSql());
                responseStream.writeColumns(sqlResult.getColumns());
                while (sqlResult.next()) responseStream.writeRow(sqlResult.getRow());
                responseStream.closeResult();
            } else {
                responseStream.startResult("result");
                responseStream.writeColumns(Arrays.asList(""));
                responseStream.writeRow(Arrays.asList(value.toString()));
                responseStream.closeResult();
            }
        } catch (IOException | SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

}
