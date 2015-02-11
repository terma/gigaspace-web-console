package com.github.terma.gigaspacesqlconsole;

import java.util.List;

public class StringUtils {

    private static final int MAX_FILE_NAME_LENGTH = 50;

    public static String safeCsvFileName(String sql) {
        sql = sql.replace(' ', '_');
        sql = sql.replaceAll("[^-_a-zA-Z0-9]", "");
        sql = sql.replaceAll("_{2,}", "_");
        if (sql.length() > MAX_FILE_NAME_LENGTH) sql = sql.substring(0, MAX_FILE_NAME_LENGTH - 1);
        return sql + ".csv";
    }

    static String toCsvRow(List<String> row) {
        final StringBuilder sb = new StringBuilder();

        boolean first = true;
        for (String value : row) {
            if (first) first = false;
            else sb.append(',');

            sb.append(value);
        }

        return sb.toString();
    }
}
