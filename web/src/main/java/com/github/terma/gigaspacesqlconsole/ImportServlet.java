package com.github.terma.gigaspacesqlconsole;

import com.github.terma.gigaspacesqlconsole.core.ImportRequest;
import com.google.gson.Gson;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public class ImportServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        try {
            safeDoPost(request);

            response.sendRedirect(request.getContextPath());
        } catch (IOException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IOException(exception);
        }
    }

    private void safeDoPost(final HttpServletRequest request) throws Exception {
        final ServletFileUpload upload = new ServletFileUpload();
        final FileItemIterator iterator = upload.getItemIterator(request);

        ImportRequest importRequest = null;
        InputStream inputStream = null;

        while (iterator.hasNext()) {
            final FileItemStream item = iterator.next();
            final String name = item.getFieldName();
            final InputStream stream = item.openStream();

            if (item.isFormField()) {
                if ("json".equals(name)) {
                    importRequest = gson.fromJson(Streams.asString(stream), ImportRequest.class);
                }
            } else {
                inputStream = stream;
            }
        }

        if (importRequest == null) throw new IOException("Expect 'json' parameter!");
        if (inputStream == null) throw new IOException("Expect file to import!");

        ProviderResolver.getProvider(importRequest.gs).import1(importRequest, inputStream);
    }

}
