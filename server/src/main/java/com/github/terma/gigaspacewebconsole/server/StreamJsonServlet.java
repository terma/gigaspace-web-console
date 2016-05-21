/*
Copyright 2015-2016 Artem Stasiuk

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
