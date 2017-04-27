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

package com.github.terma.gigaspacewebconsole.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ObjectExecuteResponseStream implements ExecuteResponseStream {

    private List<String> columns = new ArrayList<>();
    private List<ObjectRow> data = new ArrayList<>();

    @Override
    public void writeHeader(List<String> columns) throws IOException {
        this.columns = columns;
    }

    @Override
    public void writeRow(List<String> values, List<String> types) throws IOException {
        data.add(new ObjectRow(values, types));
    }

    @Override
    public void close() {

    }

    public List<String> getColumns() {
        return columns;
    }

    public List<ObjectRow> getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectExecuteResponseStream that = (ObjectExecuteResponseStream) o;
        return Objects.equals(columns, that.columns) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columns, data);
    }

    @Override
    public String toString() {
        return "ObjectExecuteResponseStream {" +
                "columns: " + columns +
                ", data: " + data +
                '}';
    }

}
