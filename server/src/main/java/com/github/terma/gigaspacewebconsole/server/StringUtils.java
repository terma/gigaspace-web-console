/*
Copyright 2015-2017 Artem Stasiuk

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

package com.github.terma.gigaspacewebconsole.server;

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
