package com.github.terma.gigaspacesqlconsole;

import javax.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = "/execute")
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
