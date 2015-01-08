package com.github.terma.gigaspacesqlconsole;

public class ExecuteServlet extends JsonServlet<ExecuteRequest> {

    @Override
    protected Object doJson(ExecuteRequest request) throws Exception {
        return Executor.query(request);
    }

    @Override
    protected Class<ExecuteRequest> getRequestClass() {
        return ExecuteRequest.class;
    }

}
