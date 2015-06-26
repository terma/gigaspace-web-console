/*
Copyright 2015 Artem Stasiuk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.github.terma.gigaspacewebconsole.provider.groovy;

import com.github.terma.gigaspacewebconsole.core.GroovyExecuteResponseStream;
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
                responseStream.startResult("");
                responseStream.writeColumns(Arrays.asList("result"));
                responseStream.writeRow(Arrays.asList(value.toString()));
                responseStream.closeResult();
            }
        } catch (IOException | SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

}
