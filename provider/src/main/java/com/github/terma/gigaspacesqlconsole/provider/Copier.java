package com.github.terma.gigaspacesqlconsole.provider;

import com.gigaspaces.client.iterator.IteratorScope;
import com.gigaspaces.document.SpaceDocument;
import com.github.terma.gigaspacesqlconsole.core.CopyRequest;
import com.j_spaces.core.client.GSIterator;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.IteratorBuilder;
import org.openspaces.core.space.UrlSpaceConfigurer;

import java.net.CacheResponse;

public class Copier {

    private static final int BATCH = 1000;

    public static CacheResponse copy(final CopyRequest request) throws Exception {
        final GigaSpace sourceGigaspace = gigaSpaceConnection(request);

        final UrlSpaceConfigurer urlSpaceConfigurer = new UrlSpaceConfigurer(request.targetUrl);
        urlSpaceConfigurer.userDetails(request.targetUser, request.targetPassword);
        final GigaSpace targetGigaspace = new GigaSpaceConfigurer(urlSpaceConfigurer.create()).create();

        final GSIterator iterator = new IteratorBuilder(sourceGigaspace)
                .addTemplate(new SpaceDocument("ObjectA")).iteratorScope(IteratorScope.CURRENT).create();

        while (iterator.hasNext()) {
            Object[] objects = iterator.nextBatch(BATCH);
            targetGigaspace.writeMultiple(objects);
        }

        return null;
    }

    @SuppressWarnings("deprecation")
    private static GigaSpace gigaSpaceConnection(CopyRequest request) {
        UrlSpaceConfigurer urlSpaceConfigurer = new UrlSpaceConfigurer(request.url);
        urlSpaceConfigurer.userDetails(request.user, request.password);
        return new GigaSpaceConfigurer(urlSpaceConfigurer.create()).create();
    }

}
