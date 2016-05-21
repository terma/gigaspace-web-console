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

import com.github.terma.gigaspacewebconsole.core.GroovyExecuteResponseStream;

import java.util.ArrayList;
import java.util.List;

public class ObjectGroovyExecuteResponseStream implements GroovyExecuteResponseStream {

    public List<ObjectGroovyExecuteResult> results = new ArrayList<>();

    private ObjectGroovyExecuteResult currentResult;

    @Override
    public void startResult(String header) {
        currentResult = new ObjectGroovyExecuteResult();
        currentResult.header = header;
    }

    @Override
    public void writeColumns(List<String> columns) {
        currentResult.columns = columns;
    }

    @Override
    public void writeRow(List<String> values) {
        currentResult.data.add(values);
    }

    @Override
    public void closeResult() {
        results.add(currentResult);
        currentResult = null;
    }

    @Override
    public void close() {

    }

}
