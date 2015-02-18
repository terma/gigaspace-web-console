package com.github.terma.gigaspacesqlconsole;

import com.github.terma.gigaspacesqlconsole.core.CopyRequest;

public class CopyServlet extends JsonServlet<CopyRequest> {

    @Override
    protected Object doJson(CopyRequest request) throws Exception {
        return CachedProviderResolver.getProvider(request.gs).copy(request);
    }

    @Override
    protected Class<CopyRequest> getRequestClass() {
        return CopyRequest.class;
    }

    @Override
    protected Validator<CopyRequest> getValidator() {
        return new AppVersionValidator<>();
    }

}
