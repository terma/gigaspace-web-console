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

package com.github.terma.gigaspacewebconsole.provider;

import com.gigaspaces.document.SpaceDocument;
import com.github.terma.gigaspacewebconsole.core.ExecuteRequest;
import com.github.terma.gigaspacewebconsole.core.GroovyExecuteResponseStream;
import com.github.terma.gigaspacewebconsole.provider.groovy.AdminClosure;
import com.github.terma.gigaspacewebconsole.provider.groovy.MemClosure;
import com.github.terma.gigaspacewebconsole.provider.groovy.PrintClosure;
import com.github.terma.gigaspacewebconsole.provider.groovy.SqlClosure;
import com.j_spaces.core.client.SQLQuery;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

public class GroovyExecutor {

    public static void execute
            (final ExecuteRequest request, final GroovyExecuteResponseStream responseStream) throws Exception {
        final SqlClosure sqlClosure = new SqlClosure(request);

        try {
            final Binding binding = new Binding();
            binding.setVariable("driver", GigaSpaceUtils.getGigaSpace(request));
            binding.setVariable("sql", sqlClosure);
            binding.setVariable("mem", new MemClosure(request));
            binding.setVariable("admin", new AdminClosure(request));
            final PrintClosure printClosure = new PrintClosure(responseStream);
            binding.setVariable("out", printClosure);

            final CompilerConfiguration configuration = new CompilerConfiguration();
            configureDefaultImports(configuration);

            final GroovyShell shell = new GroovyShell(
                    GroovyExecutor.class.getClassLoader(), binding, configuration);

            final Object value = shell.evaluate(request.sql);
            if (value != null) printClosure.call(value);

            responseStream.close();
        } finally {
            sqlClosure.close();
        }
    }

    private static void configureDefaultImports(CompilerConfiguration configuration) {
        final ImportCustomizer importCustomizer = new ImportCustomizer();
        importCustomizer.addImports(SpaceDocument.class.getName());
        importCustomizer.addImports(SQLQuery.class.getName());
        configuration.addCompilationCustomizers(importCustomizer);
    }

}
