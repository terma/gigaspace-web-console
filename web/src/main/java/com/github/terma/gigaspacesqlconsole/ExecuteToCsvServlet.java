package com.github.terma.gigaspacesqlconsole;

import com.github.terma.gigaspacesqlconsole.core.ExecuteRequest;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ExecuteToCsvServlet extends HttpServlet {

    private static final String JSON_PARAMETER = "json";
    private static final String OCTET_STREAM_CONTENT_TYPE = "application/octet-stream";

    private final Gson gson = new Gson();

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        String requestJson = getRequestJson(request);
        final ExecuteRequest req = gson.fromJson(requestJson, ExecuteRequest.class);

        response.setContentType(OCTET_STREAM_CONTENT_TYPE);

        response.getWriter().append(requestJson);

//        try {
//            doProcess(req, res);
//        } catch (final Throwable exception) {
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            response.getWriter().append(gson.toJson(new ExecuteException(exception)));
//        }
    }

    private static String getRequestJson(final HttpServletRequest request) throws IOException {
        return request.getParameter(JSON_PARAMETER);
    }

}
