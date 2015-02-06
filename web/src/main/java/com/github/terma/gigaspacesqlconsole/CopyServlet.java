package com.github.terma.gigaspacesqlconsole;

import com.github.terma.gigaspacesqlconsole.core.CopyRequest;

public class CopyServlet extends JsonWithCorrectAppVersionServlet<CopyRequest> {

    @Override
    protected Object doJsonWithCorrectAppVersion(CopyRequest request) throws Exception {
        return CachedProviderResolver.getProvider(request.gs).copy(request);
    }

    @Override
    protected Class<CopyRequest> getRequestClass() {
        return CopyRequest.class;
    }

}
