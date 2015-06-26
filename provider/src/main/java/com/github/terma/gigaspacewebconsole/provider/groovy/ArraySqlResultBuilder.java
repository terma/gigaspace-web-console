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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArraySqlResultBuilder {

    private final String sql;
    private final List<String> columns;
    private final List<List<String>> data;

    public ArraySqlResultBuilder(final String sql, final List<String> columns) {
        this.sql = sql;
        this.columns = columns;
        this.data = new ArrayList<>();
    }

    public ArraySqlResultBuilder add(String... rows) {
        data.add(Arrays.asList(rows));
        return this;
    }

    public ArraySqlResult createResult() {
        return new ArraySqlResult(sql, columns, data);
    }

}
