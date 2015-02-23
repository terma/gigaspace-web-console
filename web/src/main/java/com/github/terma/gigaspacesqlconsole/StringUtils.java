package com.github.terma.gigaspacesqlconsole;

public class StringUtils {

    private static final int MAX_FILE_NAME_LENGTH = 50;

    public static String safeCsvFileName(String sql) {
        sql = sql.replace(' ', '_');
        sql = sql.replaceAll("[^-_a-zA-Z0-9]", "");
        sql = sql.replaceAll("_{2,}", "_");
        if (sql.length() > MAX_FILE_NAME_LENGTH) sql = sql.substring(0, MAX_FILE_NAME_LENGTH - 1);
        return sql + ".csv";
    }

}
