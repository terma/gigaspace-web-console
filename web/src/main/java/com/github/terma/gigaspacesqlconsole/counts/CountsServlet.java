package com.github.terma.gigaspacesqlconsole.counts;

import com.github.terma.gigaspacesqlconsole.AppVersionValidator;
import com.github.terma.gigaspacesqlconsole.JsonServlet;
import com.github.terma.gigaspacesqlconsole.Validator;
import com.github.terma.gigaspacesqlconsole.core.CountsRequest;

public class CountsServlet extends JsonServlet<CountsRequest> {

    @Override
    protected Object doJson(CountsRequest request) throws Exception {
        return Counts.counts(request);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class getRequestClass() {
        return CountsRequest.class;
    }


    @Override
    protected Validator<CountsRequest> getValidator() {
        return new AppVersionValidator<>();
    }

}
