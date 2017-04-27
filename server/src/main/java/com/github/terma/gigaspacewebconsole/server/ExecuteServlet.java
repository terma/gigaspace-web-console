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

import com.github.terma.gigaspacewebconsole.core.ExecuteRequest;
import com.github.terma.gigaspacewebconsole.core.ExecuteResponseStream;
import com.github.terma.gigaspacewebconsole.core.JsonExecuteResponseStream;

import java.io.PrintWriter;

public class ExecuteServlet extends StreamJsonServlet<ExecuteRequest> {

    @Override
    protected void doPost(final ExecuteRequest request, final PrintWriter writer) throws Exception {
        final ExecuteResponseStream responseStream = new JsonExecuteResponseStream(writer);
        CachedProviderResolver.getProvider(request.driver).query(request, responseStream);
    }

    @Override
    protected Class<ExecuteRequest> getRequestClass() {
        return ExecuteRequest.class;
    }

    @Override
    protected Validator<ExecuteRequest> getValidator() {
        return new AppVersionValidator<>();
    }

}
