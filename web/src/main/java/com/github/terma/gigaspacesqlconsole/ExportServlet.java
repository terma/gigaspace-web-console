/*
Copyright 2015 Artem Stasiuk

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
        response.addHeader("Content-Disposition", "attachment; filename=\"export.zip\"");

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
