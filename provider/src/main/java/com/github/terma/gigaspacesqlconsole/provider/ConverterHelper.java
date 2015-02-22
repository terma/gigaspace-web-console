package com.github.terma.gigaspacesqlconsole.provider;

import com.github.terma.gigaspacesqlconsole.core.config.Config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConverterHelper {

    private static final String CONVERTER_METHOD = "convert";

    private static final List<Method> converterMethods = new ArrayList<>();

    static {
        final Config config = Config.read();

        for (final String converterClassName : config.user.converters) {
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
                        " in class: " + converterClassName + " with one Object type parameter!");
            }

            converterMethods.add(method);
        }
    }

    public static String getFormattedValue(final Object rawValue, final String stringValue) throws SQLException {
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

    public static String getFormattedValue(final ResultSet resultSet, final String column) throws SQLException {
        final Object rawValue = resultSet.getObject(column);
        return getFormattedValue(rawValue, resultSet.getString(column));
    }

}
