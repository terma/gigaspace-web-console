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

package com.github.terma.gigaspacewebconsole.provider.executor.gigaspace;

import com.github.terma.gigaspacewebconsole.core.config.ConfigLocator;
import com.github.terma.gigaspacewebconsole.provider.ConverterHelper;
import com.github.terma.gigaspacewebconsole.provider.EmbeddedObjectsConverter;
import com.github.terma.gigaspacewebconsole.provider.executor.Executor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GigaSpaceExecutor {

    public static final ConverterHelper CONVERTER_HELPER = buildConverterHelper();

    public static final Executor INSTANCE = new Executor(
            new GigaSpaceConnectionFactory(),
            new TimestampPreprocessor(),
            Arrays.asList(
                    new PropertySelectExecutorPlugin(),
                    new ExecutorPluginUpdate(),
                    new ExecutorPluginGenerate()),
            CONVERTER_HELPER
    );

    private static ConverterHelper buildConverterHelper() {
        List<String> converters = new ArrayList<>();
        converters.addAll(ConfigLocator.CONFIG.user.converters);
        converters.add(EmbeddedObjectsConverter.class.getName());
        return new ConverterHelper(converters);
    }

}
