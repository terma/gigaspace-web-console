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

package com.github.terma.gigaspacewebconsole;

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
