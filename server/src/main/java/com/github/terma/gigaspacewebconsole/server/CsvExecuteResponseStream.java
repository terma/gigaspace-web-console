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

import com.github.terma.gigaspacewebconsole.core.ExecuteResponseStream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class CsvExecuteResponseStream implements ExecuteResponseStream {

    private final CSVPrinter csvPrinter;

    public CsvExecuteResponseStream(final Writer writer) throws IOException {
        this.csvPrinter = CSVFormat.DEFAULT.print(writer);
    }

    @Override
    public void writeHeader(final List<String> columns) throws IOException {
        csvPrinter.printRecord(columns);
    }

    @Override
    public void writeRow(final List<String> values, List<String> types) throws IOException {
        csvPrinter.printRecord(values);
    }

    @Override
    public void close() {

    }

}
