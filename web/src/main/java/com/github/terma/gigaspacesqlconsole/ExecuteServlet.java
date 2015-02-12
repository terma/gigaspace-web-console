package com.github.terma.gigaspacesqlconsole;

import com.github.terma.gigaspacesqlconsole.core.ExecuteRequest;
import com.github.terma.gigaspacesqlconsole.core.ObjectExecuteResponseStream;

public class ExecuteServlet extends JsonWithCorrectAppVersionServlet<ExecuteRequest> {

    @Override
    protected Object doJsonWithCorrectAppVersion(ExecuteRequest request) throws Exception {
        final ObjectExecuteResponseStream responseStream = new ObjectExecuteResponseStream();
        CachedProviderResolver.getProvider(request.gs).query(request, responseStream);
        return responseStream;
    }

    @Override
    protected Class<ExecuteRequest> getRequestClass() {
        return ExecuteRequest.class;
    }

}
