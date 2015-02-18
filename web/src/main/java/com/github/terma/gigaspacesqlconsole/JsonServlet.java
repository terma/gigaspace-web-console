package com.github.terma.gigaspacesqlconsole;

import com.google.gson.Gson;

import java.io.PrintWriter;

public abstract class JsonServlet<T> extends StreamJsonServlet<T> {

    private final Gson gson = new Gson();

    @Override
    protected final void doPost(final T request, final PrintWriter writer) throws Exception {
        writer.append(gson.toJson(doJson(request)));
    }

    protected abstract Object doJson(final T request) throws Exception;

}
