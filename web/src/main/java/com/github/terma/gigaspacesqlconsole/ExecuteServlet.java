package com.github.terma.gigaspacesqlconsole;

import com.github.terma.gigaspacesqlconsole.core.ExecuteRequest;

public class ExecuteServlet extends JsonWithCorrectAppVersionServlet<ExecuteRequest> {

    @Override
    protected Object doJsonWithCorrectAppVersion(ExecuteRequest request) throws Exception {
        return CachedProviderResolver.getProvider(request.gsVersion).query(request);
    }

    @Override
    protected Class<ExecuteRequest> getRequestClass() {
        return ExecuteRequest.class;
    }

}
