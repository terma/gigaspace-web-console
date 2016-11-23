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

package com.github.terma.gigaspacewebconsole.provider;

import com.github.terma.gigaspacewebconsole.core.*;
import com.github.terma.gigaspacewebconsole.provider.executor.DatabaseExecutor;

import java.io.InputStream;
import java.io.OutputStream;

@SuppressWarnings("UnusedDeclaration")
public class DatabaseProvider implements Provider {

    @Override
    public CountsResponse counts(CountsRequest request) {
        throw new UnsupportedOperationException("We don't support count for DB, please wait future releases!");
    }

    @Override
    public void query(ExecuteRequest request, ExecuteResponseStream responseStream) throws Exception {
        DatabaseExecutor.INSTANCE.execute(request, responseStream);
    }

    @Override
    public CopyResponse copy(final CopyRequest request) throws Exception {
        throw new UnsupportedOperationException("We don't support copy for DB, please wait future releases!");
    }

    @Override
    public void groovyExecute(ExecuteRequest request, GroovyExecuteResponseStream responseStream) throws Exception {
        throw new UnsupportedOperationException("We don't support groovy for DB, please wait future releases!");
    }

    @Override
    public void export(ExportRequest request, OutputStream outputStream) throws Exception {
        throw new UnsupportedOperationException("We don't support export for DB, please wait future releases!");
    }

    @Override
    public void import1(ImportRequest request, InputStream inputStream) throws Exception {
        throw new UnsupportedOperationException("We don't support import for DB, please wait future releases!");
    }

    @Override
    public ExploreResponse explore(ExploreRequest request) throws Exception {
        throw new UnsupportedOperationException("We don't support explore for DB, please wait future releases!");
    }

}
