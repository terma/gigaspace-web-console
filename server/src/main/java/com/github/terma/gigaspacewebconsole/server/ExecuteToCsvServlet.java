/*
Copyright 2015-2017 Artem Stasiuk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.github.terma.gigaspacewebconsole.server;

import com.github.terma.gigaspacewebconsole.core.ExecuteRequest;
import com.github.terma.gigaspacewebconsole.core.ExecuteResponseStream;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

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
        final ExecuteResponseStream responseStream = new CsvExecuteResponseStream(writer);

        try {
            CachedProviderResolver.getProvider(req.driver).query(req, responseStream);
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
