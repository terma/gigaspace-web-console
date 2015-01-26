package com.github.terma.gigaspacesqlconsole;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class JsonServlet<T> extends HttpServlet {

    private static final String JSON_CONTENT_TYPE = "application/json";

    private final Gson gson = new Gson();

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        final T executeRequest = gson.fromJson(getRequestBody(request), getRequestClass());
        response.setContentType(JSON_CONTENT_TYPE);
        try {
            response.getWriter().append(gson.toJson(doJson(executeRequest)));
        } catch (final Throwable exception) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().append(gson.toJson(new ExecuteException(exception)));
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
