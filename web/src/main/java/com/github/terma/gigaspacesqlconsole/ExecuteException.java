package com.github.terma.gigaspacesqlconsole;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class ExecuteException {

    public final String exceptionClass;
    public final String message;
    public final String stacktrace;

    public ExecuteException(final Throwable e) {
        exceptionClass = e.getClass().getCanonicalName();
        message = e.getMessage();

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream ps = new PrintStream(out);
        e.printStackTrace(ps);
        stacktrace = out.toString();
    }

}
