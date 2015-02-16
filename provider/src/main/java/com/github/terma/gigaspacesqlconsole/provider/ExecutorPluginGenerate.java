package com.github.terma.gigaspacesqlconsole.provider;

import com.gigaspaces.client.WriteModifiers;
import com.gigaspaces.document.SpaceDocument;
import com.github.terma.gigaspacesqlconsole.core.ExecuteRequest;
import com.github.terma.gigaspacesqlconsole.core.ExecuteResponseStream;

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
