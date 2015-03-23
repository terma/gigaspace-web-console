package com.github.terma.gigaspacesqlconsole;

import com.github.terma.gigaspacesqlconsole.core.ExportRequest;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class ExportServlet extends HttpServlet {

    private static final String JSON_PARAMETER = "json";
    private static final String FILE_CONTENT_TYPE = "application/stream";

    private final Gson gson = new Gson();

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        final ExportRequest req = gson.fromJson(getRequestJson(request), ExportRequest.class);

        response.setContentType(FILE_CONTENT_TYPE);
        response.addHeader("Content-Disposition", "attachment; filename=\"export\"");

        final OutputStream outputStream = response.getOutputStream();

        try {
            CachedProviderResolver.getProvider(req.gs).export(req, outputStream);
        } catch (final Throwable exception) {
            throw new IOException(exception);
        }
    }

    private static String getRequestJson(final HttpServletRequest request) throws IOException {
        return request.getParameter(JSON_PARAMETER);
    }

}
