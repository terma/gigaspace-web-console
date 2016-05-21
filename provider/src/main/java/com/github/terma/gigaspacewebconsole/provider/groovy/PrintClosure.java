/*
Copyright 2015-2016 Artem Stasiuk

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

import com.gigaspaces.document.SpaceDocument;
import com.github.terma.gigaspacewebconsole.core.GroovyExecuteResponseStream;
import com.github.terma.gigaspacewebconsole.provider.ConverterHelper;
import com.github.terma.gigaspacewebconsole.provider.IterableUtils;
import com.github.terma.gigaspacewebconsole.provider.SqlResult;
import com.github.terma.gigaspacewebconsole.provider.executor.gigaspace.GigaSpaceExecutor;
import groovy.lang.Closure;

import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.*;

public class PrintClosure extends Closure {

    private static final ConverterHelper converterHelper = GigaSpaceExecutor.CONVERTER_HELPER;
    private final GroovyExecuteResponseStream responseStream;

    public PrintClosure(final GroovyExecuteResponseStream responseStream) {
        super(null);
        this.responseStream = responseStream;
    }

    private static void print(final Object value, final GroovyExecuteResponseStream responseStream) {
        try {
            if (value instanceof SpaceDocument) {
                printSpaceDocument(value, responseStream);
            } else if (isIterableOrArrayOfSpaceDocument(value)) {
                printIterableOrArrayOfSpaceDocument(value, responseStream);
            } else if (value != null && value.getClass().isArray()) {
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

    private static void printSpaceDocument(
            final Object value, final GroovyExecuteResponseStream responseStream)
            throws IOException, SQLException {
        printIterableOrArrayOfSpaceDocument(new Object[]{value}, responseStream);
    }

    private static void printIterableOrArrayOfSpaceDocument(
            final Object value, final GroovyExecuteResponseStream responseStream) throws IOException, SQLException {
        responseStream.startResult("result: " + value);

        final Iterable iterable = IterableUtils.asIterable(value);

        final LinkedHashSet<String> columns = new LinkedHashSet<>();
        for (final Object item : iterable) {
            SpaceDocument spaceDocument = (SpaceDocument) item;
            if (spaceDocument != null) columns.addAll(spaceDocument.getProperties().keySet());
        }
        responseStream.writeColumns(new ArrayList<>(columns));

        for (final Object item : iterable) {
            final SpaceDocument spaceDocument = (SpaceDocument) item;
            if (spaceDocument != null) {
                final List<String> row = new ArrayList<>(columns.size());
                for (String column : columns)
                    row.add(converterHelper.getFormattedValue(spaceDocument.getProperty(column)));
                responseStream.writeRow(row);
            } else {
                final List<String> row = new ArrayList<>(columns.size());
                for (String ignored : columns) row.add(null);
                responseStream.writeRow(row);
            }
        }

        responseStream.closeResult();
    }

    private static boolean isIterableOrArrayOfSpaceDocument(Object value) {
        if (value != null && (value.getClass().isArray() || value instanceof Iterable)) {
            final Iterable iterable = IterableUtils.asIterable(value);
            for (final Object item : iterable)
                if (item != null && !(item instanceof SpaceDocument)) return false;
            return true;
        }
        return false;
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
