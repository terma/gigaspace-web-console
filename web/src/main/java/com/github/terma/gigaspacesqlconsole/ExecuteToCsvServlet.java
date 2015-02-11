package com.github.terma.gigaspacesqlconsole;

import com.github.terma.gigaspacesqlconsole.core.ExecuteRequest;
import com.github.terma.gigaspacesqlconsole.core.ExecuteResponse;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ExecuteToCsvServlet extends HttpServlet {

    private static final String JSON_PARAMETER = "json";
    private static final String CSV_CONTENT_TYPE = "text/csv";

    private final Gson gson = new Gson();

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        final ExecuteRequest req = gson.fromJson(getRequestJson(request), ExecuteRequest.class);

        response.setContentType(CSV_CONTENT_TYPE);
//        response.addHeader("Content-Transfer-Encoding", "binary");
        response.addHeader("Content-Disposition", "attachment; filename=\"" + StringUtils.safeCsvFileName(req.sql) + "\"");

        final PrintWriter writer = response.getWriter();
        try {
            ExecuteResponse res = CachedProviderResolver.getProvider(req.gs).query(req);

            writer.append(StringUtils.toCsvRow(res.columns)).append('\n');
            for (final List<String> row : res.data) {
                writer.append(StringUtils.toCsvRow(row)).append('\n');
            }

        } catch (final Throwable exception) {
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writer.append(exception.getClass().getName()).append('\n');
            writer.append(exception.getMessage()).append('\n');
            exception.printStackTrace(writer);
        }
    }

    private static String getRequestJson(final HttpServletRequest request) throws IOException {
        return request.getParameter(JSON_PARAMETER);
    }

}
