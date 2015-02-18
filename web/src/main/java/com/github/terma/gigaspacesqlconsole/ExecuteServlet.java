package com.github.terma.gigaspacesqlconsole;

import com.github.terma.gigaspacesqlconsole.core.ExecuteRequest;
import com.github.terma.gigaspacesqlconsole.core.ExecuteResponseStream;
import com.github.terma.gigaspacesqlconsole.core.JsonExecuteResponseStream;

import java.io.PrintWriter;

public class ExecuteServlet extends StreamJsonServlet<ExecuteRequest> {

    @Override
    protected void doPost(final ExecuteRequest request, final PrintWriter writer) throws Exception {
        final ExecuteResponseStream responseStream = new JsonExecuteResponseStream(writer);
        CachedProviderResolver.getProvider(request.gs).query(request, responseStream);
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
