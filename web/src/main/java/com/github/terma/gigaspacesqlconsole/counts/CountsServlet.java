package com.github.terma.gigaspacesqlconsole.counts;

import com.github.terma.gigaspacesqlconsole.JsonWithCorrectAppVersionServlet;

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
