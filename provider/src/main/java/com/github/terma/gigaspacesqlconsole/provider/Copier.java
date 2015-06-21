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


package com.github.terma.gigaspacesqlconsole.provider;

import com.gigaspaces.client.iterator.IteratorScope;
import com.gigaspaces.document.SpaceDocument;
import com.github.terma.gigaspacesqlconsole.core.CopyRequest;
import com.github.terma.gigaspacesqlconsole.core.CopyResponse;
import com.j_spaces.core.client.ExternalEntry;
import com.j_spaces.core.client.GSIterator;
import com.j_spaces.core.client.SQLQuery;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.IteratorBuilder;

import java.util.logging.Logger;

import static com.github.terma.gigaspacesqlconsole.provider.GigaSpaceUtils.getGigaSpace;

public class Copier {

    private static final Logger LOGGER = Logger.getLogger(Copier.class.getSimpleName());

    private static final int BATCH = 1000;

    @SuppressWarnings("deprecation")
    public static CopyResponse copy(final CopyRequest request) throws Exception {
        LOGGER.info("start copy: " + request.sql);

        final CopySql copySql = CopySqlParser.parse(request.sql);

        final GigaSpace sourceGigaspace = getGigaSpace(request);
        final GigaSpace targetGigaspace = getGigaSpace(request.targetUrl, request.targetUser, request.targetPassword);

        final SQLQuery sqlQuery = new SQLQuery(copySql.typeName, copySql.where);

        final GSIterator iterator = new IteratorBuilder(sourceGigaspace)
                .addTemplate(sqlQuery).iteratorScope(IteratorScope.CURRENT).create();

        final BufferedWriter bufferedWriter = new BufferedWriter(BATCH, targetGigaspace);

        int index = -1;
        int total = 0;

        final int from = copySql.from != null ? copySql.from : 0;
        final int to = copySql.only != null ? from + copySql.only : Integer.MAX_VALUE;

        while (iterator.hasNext()) {
            final Object object = iterator.next();
            index++;

            if (from > index || index >= to) continue;

            if (!copySql.reset.isEmpty()) {
                if (object instanceof SpaceDocument) {
                    for (final String resetField : copySql.reset) {
                        ((SpaceDocument) object).removeProperty(resetField);
                    }
                } else if (object instanceof ExternalEntry) {
                    final ExternalEntry externalEntry = (ExternalEntry) object;
                    for (final String resetField : copySql.reset) {
                        externalEntry.setFieldValue(resetField, null);
                    }
                }
            }

            total++;
            bufferedWriter.write(object);
        }

        bufferedWriter.flush();

        LOGGER.info("Copied " + total + " for " + request.sql);

        final CopyResponse response = new CopyResponse();
        response.count = total;
        return response;
    }

}
