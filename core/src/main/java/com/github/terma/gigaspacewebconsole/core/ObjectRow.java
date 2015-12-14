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

package com.github.terma.gigaspacewebconsole.core;

import java.util.List;

public class ObjectRow {

    private final List<String> values;
    private final List<String> types;

    public ObjectRow(List<String> values, List<String> types) {
        this.values = values;
        this.types = types;
    }

    public List<String> getValues() {
        return values;
    }

    public List<String> getTypes() {
        return types;
    }

    @Override
    public String toString() {
        return "Row {" +
                "values: " + values +
                ", types: " + types +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjectRow row = (ObjectRow) o;
        return values.equals(row.values) && types.equals(row.types);
    }

    @Override
    public int hashCode() {
        int result = values.hashCode();
        result = 31 * result + types.hashCode();
        return result;
    }
}
