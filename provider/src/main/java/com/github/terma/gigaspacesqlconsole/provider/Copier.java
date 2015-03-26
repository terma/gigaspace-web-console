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

    public static CopyResponse copy(final CopyRequest request) throws Exception {
        LOGGER.info("start copy: " + request.sql);

        final CopySql copySql = CopySqlParser.parse(request.sql);

        final GigaSpace sourceGigaspace = getGigaSpace(request);
        final GigaSpace targetGigaspace = getGigaSpace(request.targetUrl, request.targetUser, request.targetPassword);

        final SQLQuery sqlQuery = new SQLQuery(copySql.typeName, copySql.where);

        final GSIterator iterator = new IteratorBuilder(sourceGigaspace)
                .addTemplate(sqlQuery).iteratorScope(IteratorScope.CURRENT).create();

        int total = 0;
        while (iterator.hasNext()) {
            Object[] objects = iterator.nextBatch(BATCH);

            if (!copySql.reset.isEmpty()) {
                for (final Object object : objects) {
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
            }

            total += objects.length;
            targetGigaspace.writeMultiple(objects);
        }

        LOGGER.info("Copied " + total + " for " + request.sql);

        final CopyResponse response = new CopyResponse();
        response.count = total;
        return response;
    }

}
