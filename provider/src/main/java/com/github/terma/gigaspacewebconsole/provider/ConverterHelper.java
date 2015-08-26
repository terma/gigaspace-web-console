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

package com.github.terma.gigaspacewebconsole.provider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConverterHelper {

    private static final String CONVERTER_METHOD = "convert";

    private final List<Method> converterMethods = new ArrayList<>();

    public ConverterHelper(List<String> converters) {
        for (final String converterClassName : converters) {
            final Class converterClass;
            try {
                converterClass = Class.forName(converterClassName);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Can't load converter class: " + converterClassName);
            }

            final Method method;
            try {
                method = converterClass.getMethod(CONVERTER_METHOD, Object.class);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("Can't find converter method: " + CONVERTER_METHOD +
                        " in class: " + converterClassName + " with one Object driver parameter!");
            }

            converterMethods.add(method);
        }
    }

    public String getFormattedValue(final Object rawValue, final String stringValue) throws SQLException {
        for (final Method convertMethod : converterMethods) {
            try {
                final String value = (String) convertMethod.invoke(null, rawValue);
                if (value != null) return value;
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalArgumentException("Can't convert!", e);
            }
        }

        return stringValue;
    }

    public String getFormattedValue(final Object rawValue) throws SQLException {
        String stringValue = rawValue == null ? null : rawValue.toString();
        return getFormattedValue(rawValue, stringValue);
    }

    public String getFormattedValue(final ResultSet resultSet, final String column) throws SQLException {
        final Object rawValue = resultSet.getObject(column);
        return getFormattedValue(rawValue, resultSet.getString(column));
    }

}
