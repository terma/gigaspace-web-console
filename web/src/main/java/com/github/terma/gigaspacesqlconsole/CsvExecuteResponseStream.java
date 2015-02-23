package com.github.terma.gigaspacesqlconsole;

import com.github.terma.gigaspacesqlconsole.core.ExecuteResponseStream;
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
    public void writeRow(final List<String> values) throws IOException {
        csvPrinter.printRecord(values);
    }

    @Override
    public void close() {

    }

}
