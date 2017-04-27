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

package com.github.terma.gigaspacewebconsole.provider;

import com.github.terma.gigaspacewebconsole.core.*;
import com.github.terma.gigaspacewebconsole.provider.executor.gigaspace.GigaSpaceExecutor;

import java.io.InputStream;
import java.io.OutputStream;

@SuppressWarnings("UnusedDeclaration")
public class GigaSpaceProvider implements Provider {

    private final Counts countsProvider = new Counts();

    @Override
    public CountsResponse counts(final CountsRequest request) {
        return countsProvider.counts(request);
    }

    @Override
    public void query(final ExecuteRequest request, final ExecuteResponseStream responseStream) throws Exception {
        GigaSpaceExecutor.INSTANCE.execute(request, responseStream);
    }

    @Override
    public CopyResponse copy(final CopyRequest request) throws Exception {
        return Copier.copy(request);
    }

    @Override
    public void groovyExecute(final ExecuteRequest request, final GroovyExecuteResponseStream responseStream) throws Exception {
        GroovyExecutor.execute(request, responseStream);
    }

    @Override
    public void export(final ExportRequest request, final OutputStream outputStream) throws Exception {
        Exporter.execute(request, outputStream);
    }

    @Override
    public void import1(final ImportRequest request, final InputStream inputStream) throws Exception {
        Importer.execute(request, inputStream);
    }

    @Override
    public ExploreResponse explore(final ExploreRequest request) throws Exception {
        return Explorer.explore(request);
    }

}
