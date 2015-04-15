package com.github.terma.gigaspacesqlconsole.provider;

import com.github.terma.gigaspacesqlconsole.core.*;

import java.io.InputStream;
import java.io.OutputStream;

@SuppressWarnings("UnusedDeclaration")
public class ProviderImpl implements Provider {

    private Counts countsProvider = new Counts();

    @Override
    public CountsResponse counts(CountsRequest request) {
        return countsProvider.counts(request);
    }

    @Override
    public void query(ExecuteRequest request, ExecuteResponseStream responseStream) throws Exception {
        Executor.query(request, responseStream);
    }

    @Override
    public CopyResponse copy(final CopyRequest request) throws Exception {
        return Copier.copy(request);
    }

    @Override
    public void execute(ExecuteRequest request, GroovyExecuteResponseStream responseStream) throws Exception {
        GroovyExecutor.execute(request, responseStream);
    }

    @Override
    public void export(ExportRequest request, OutputStream outputStream) throws Exception {
        Exporter.execute(request, outputStream);
    }

    @Override
    public void import1(ImportRequest request, InputStream inputStream) throws Exception {
        Importer.execute(request, inputStream);
    }

}
