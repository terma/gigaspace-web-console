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

package com.github.terma.gigaspacewebconsole.provider.executor.gigaspace;

import com.gigaspaces.client.WriteModifiers;
import com.gigaspaces.document.SpaceDocument;
import com.github.terma.gigaspacewebconsole.core.ExecuteRequest;
import com.github.terma.gigaspacewebconsole.core.ExecuteResponseStream;
import com.github.terma.gigaspacewebconsole.provider.driver.GigaSpaceUtils;
import com.github.terma.gigaspacewebconsole.provider.executor.ExecutorPlugin;

import java.io.IOException;
import java.util.Map;

import static java.util.Arrays.asList;

class ExecutorPluginGenerate implements ExecutorPlugin {

    @Override
    public boolean execute(final ExecuteRequest request, final ExecuteResponseStream responseStream) throws IOException {
        final GenerateSql generateSql = GenerateSqlParser.parse(request.sql);
        if (generateSql == null) return false;

        final SpaceDocument[] spaceDocuments = new SpaceDocument[generateSql.count];
        for (int i = 0; i < spaceDocuments.length; i++) {
            SpaceDocument spaceDocument = new SpaceDocument(generateSql.typeName);
            for (Map.Entry<String, Object> field : generateSql.fields.entrySet()) {
                spaceDocument.setProperty(field.getKey(), field.getValue());
            }
            spaceDocuments[i] = spaceDocument;
        }

        GigaSpaceUtils.getGigaSpace(request).writeMultiple(spaceDocuments, WriteModifiers.NONE);

        responseStream.writeHeader(asList("affected_rows"));
        responseStream.writeRow(asList(Integer.toString(spaceDocuments.length)));
        responseStream.close();
        return true;
    }


}
