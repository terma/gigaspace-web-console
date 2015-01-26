package com.github.terma.gigaspacesqlconsole.provider;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;

import java.util.concurrent.atomic.AtomicInteger;

public class GigaSpaceUtils {

    private static final AtomicInteger idGenerator = new AtomicInteger();

    public static void registerType(GigaSpace gigaSpace, final String typeName) {
        SpaceTypeDescriptor typeDescriptor = new SpaceTypeDescriptorBuilder(typeName)
                .idProperty("A").create();
        gigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);
    }

    public static void createDocument(GigaSpace gigaSpace, String typeName) {
        final SpaceDocument spaceDocument = new SpaceDocument(typeName);
        spaceDocument.setProperty("A", idGenerator.incrementAndGet());
        spaceDocument.setProperty("B", false);
        spaceDocument.setProperty("C", "ok");
        gigaSpace.write(spaceDocument);
    }

    public static GigaSpace getGigaSpace(String url) {
        UrlSpaceConfigurer urlSpaceConfigurer = new UrlSpaceConfigurer(url);
        return new GigaSpaceConfigurer(urlSpaceConfigurer.create()).create();
    }
}
