package com.github.terma.gigaspacesqlconsole.counts;

import com.github.terma.gigaspacesqlconsole.JsonWithCorrectAppVersionServlet;
import com.github.terma.gigaspacesqlconsole.core.CountsRequest;

public class CountsServlet extends JsonWithCorrectAppVersionServlet<CountsRequest> {

    @Override
    protected Object doJsonWithCorrectAppVersion(CountsRequest request) throws Exception {
        return Counts.counts(request);
    }

    @Override
    protected Class getRequestClass() {
        return CountsRequest.class;
    }

}
