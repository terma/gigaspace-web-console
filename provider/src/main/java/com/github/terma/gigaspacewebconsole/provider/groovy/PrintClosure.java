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
import com.github.terma.gigaspacewebconsole.provider.ConverterHelper;
import com.github.terma.gigaspacewebconsole.provider.SqlResult;
import com.github.terma.gigaspacewebconsole.provider.executor.gigaspace.GigaSpaceExecutor;
import groovy.lang.Closure;

import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class PrintClosure extends Closure {

    private static final ConverterHelper converterHelper = GigaSpaceExecutor.CONVERTER_HELPER;
    private final GroovyExecuteResponseStream responseStream;

    public PrintClosure(final GroovyExecuteResponseStream responseStream) {
        super(null);
        this.responseStream = responseStream;
    }

    private static void print(final Object value, final GroovyExecuteResponseStream responseStream) {
        try {
            if (value != null && value.getClass().isArray()) {
                printArray(value, responseStream);
            } else if (value instanceof Iterable) {
                printIterable(value, responseStream);
            } else if (value instanceof Map) {
                printMap(value, responseStream);
            } else if (value instanceof SqlResult) {
                printSqlResult((SqlResult) value, responseStream);
            } else {
                printAny(value, responseStream);
            }
        } catch (IOException | SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static void printAny(Object value, GroovyExecuteResponseStream responseStream)
            throws IOException, SQLException {
        responseStream.startResult("");
        responseStream.writeColumns(Collections.singletonList("result"));
        responseStream.writeRow(Collections.singletonList(converterHelper.getFormattedValue(value)));
        responseStream.closeResult();
    }

    private static void printSqlResult(SqlResult value, GroovyExecuteResponseStream responseStream)
            throws IOException, SQLException {
        responseStream.startResult(value.getSql());
        responseStream.writeColumns(value.getColumns());
        while (value.next()) responseStream.writeRow(value.getRow());
        responseStream.closeResult();
    }

    private static void printArray(Object value, GroovyExecuteResponseStream responseStream)
            throws IOException, SQLException {
        responseStream.startResult("");
        responseStream.writeColumns(Collections.singletonList("result: " + value.getClass()));
        for (int i = 0; i < Array.getLength(value); i++) {
            final String formattedValue = converterHelper.getFormattedValue(Array.get(value, i));
            responseStream.writeRow(Collections.singletonList(formattedValue));
        }
        responseStream.closeResult();
    }

    private static void printIterable(Object value, GroovyExecuteResponseStream responseStream)
            throws IOException, SQLException {
        responseStream.startResult("");
        responseStream.writeColumns(Collections.singletonList("result: " + value.getClass()));
        final Iterable iterable = ((Iterable) value);
        for (Object item : iterable) {
            final String formattedValue = converterHelper.getFormattedValue(item);
            responseStream.writeRow(Collections.singletonList(formattedValue));
        }
        responseStream.closeResult();
    }

    private static void printMap(Object value, GroovyExecuteResponseStream responseStream)
            throws IOException, SQLException {
        responseStream.startResult(value.getClass().getName());
        responseStream.writeColumns(Arrays.asList("key", "value"));
        @SuppressWarnings("unchecked")
        final Map<Object, Object> map = (Map<Object, Object>) value;
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            responseStream.writeRow(Arrays.asList(
                    converterHelper.getFormattedValue(entry.getKey()),
                    converterHelper.getFormattedValue(entry.getValue())
            ));
        }
        responseStream.closeResult();
    }

    @Override
    public Object call(final Object[] arguments) {
        print(arguments, responseStream);
        return null;
    }

    @Override
    public Object call(final Object argument) {
        print(argument, responseStream);
        return null;
    }

}
