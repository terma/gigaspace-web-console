package com.github.terma.gigaspacesqlconsole;

import com.github.terma.gigaspacesqlconsole.core.ExecuteRequest;
import com.github.terma.gigaspacesqlconsole.core.GroovyExecuteResponseStream;

import java.io.PrintWriter;

public class GroovyExecuteServlet extends StreamJsonServlet<ExecuteRequest> {

    @Override
    protected void doPost(final ExecuteRequest request, final PrintWriter writer) throws Exception {
        final GroovyExecuteResponseStream responseStream = new JsonGroovyExecuteResponseStream(writer);
        CachedProviderResolver.getProvider(request.gs).execute(request, responseStream);
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
