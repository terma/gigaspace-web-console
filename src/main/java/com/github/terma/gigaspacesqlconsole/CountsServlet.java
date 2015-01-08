package com.github.terma.gigaspacesqlconsole;

import javax.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = "/counts")
public class CountsServlet extends JsonServlet<CountsRequest> {

    @Override
    protected Object doJson(CountsRequest request) throws Exception {
        return CountGetter.counts(request);
    }

    @Override
    protected Class getRequestClass() {
        return CountsRequest.class;
    }

}
