package com.github.terma.gigaspacesqlconsole;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class StreamJsonServlet<T> extends HttpServlet {

    private static final String JSON_CONTENT_TYPE = "application/json";

    private static final String ERROR_DELIMITER = "/* --- JSON STREAM --- ERROR DELIMITER --- */";

    private final Gson gson = new Gson();

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        final T executeRequest = gson.fromJson(getRequestBody(request), getRequestClass());
        response.setContentType(JSON_CONTENT_TYPE);

        final PrintWriter writer = response.getWriter();
        try {
            getValidator().validate(executeRequest);
            doPost(executeRequest, writer);
        } catch (final Throwable exception) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writer.append(ERROR_DELIMITER).append('\n');
            writer.append(gson.toJson(new ExecuteException(exception)));
        }
    }

    protected abstract void doPost(final T request, PrintWriter writer) throws Exception;

    protected abstract Class<T> getRequestClass();

    protected Validator<T> getValidator() {
        return new EmptyValidator<>();
    }

    private static String getRequestBody(final HttpServletRequest request) throws IOException {
        final StringBuilder requestBody = new StringBuilder();

        while (true) {
            String line = request.getReader().readLine();
            if (line == null) break;
            else requestBody.append(line).append('\n');
        }

        return requestBody.toString();
    }

}
