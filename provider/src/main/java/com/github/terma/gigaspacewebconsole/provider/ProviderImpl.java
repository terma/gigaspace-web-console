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

package com.github.terma.gigaspacewebconsole.provider;

import com.github.terma.gigaspacewebconsole.provider.executor.Executor;
import com.github.terma.gigaspacewebconsole.core.*;

import java.io.InputStream;
import java.io.OutputStream;

@SuppressWarnings("UnusedDeclaration")
public class ProviderImpl implements Provider {

    private Counts countsProvider = new Counts();

    @Override
    public CountsResponse counts(GeneralRequest request) {
        return countsProvider.counts(request);
    }

    @Override
    public void query(ExecuteRequest request, ExecuteResponseStream responseStream) throws Exception {
        Executor.execute(request, responseStream);
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
