package com.github.terma.gigaspacesqlconsole;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class JsonServlet<T> extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final T executeRequest = gson.fromJson(getRequestBody(req), getRequestClass());
        resp.setContentType("application/json");
        try {
            resp.getWriter().append(gson.toJson(doJson(executeRequest)));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().append(gson.toJson(new ExecuteException(e)));
        }
    }

    protected abstract Object doJson(final T request) throws Exception;

    protected abstract Class<T> getRequestClass();

    private static String getRequestBody(final HttpServletRequest request) throws IOException {
        final StringBuilder requestBody = new StringBuilder();

        while (true) {
            String line = request.getReader().readLine();
            if (line == null) break;
            else requestBody.append(line).append("\n");
        }

        return requestBody.toString();
    }

}
