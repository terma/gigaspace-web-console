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

package com.github.terma.gigaspacesqlconsole.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ObjectExecuteResponseStream implements ExecuteResponseStream {

    private List<String> columns;
    private List<List<String>> data;

    @Override
    public void writeHeader(List<String> columns) throws IOException {
        this.columns = columns;
    }

    @Override
    public void writeRow(List<String> values) throws IOException {
        if (data == null) data = new ArrayList<>();
        data.add(values);
    }

    @Override
    public void close() {

    }

    public List<String> getColumns() {
        return columns;
    }

    public List<List<String>> getData() {
        return data;
    }

}
