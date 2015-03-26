package com.github.terma.gigaspacesqlconsole.provider;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;
import com.github.terma.gigaspacesqlconsole.core.ExecuteRequest;
import com.github.terma.gigaspacesqlconsole.core.GeneralRequest;
import com.j_spaces.jdbc.driver.GConnection;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

public class GigaSpaceUtils {

    private static final AtomicInteger idGenerator = new AtomicInteger();

    public static void registerType(GigaSpace gigaSpace, final String typeName) {
        SpaceTypeDescriptor typeDescriptor = new SpaceTypeDescriptorBuilder(typeName)
                .idProperty("A").create();
        gigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);
    }

    public static void writeDocument(GigaSpace gigaSpace, String typeName) {
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

    public static GigaSpace getGigaSpace(final GeneralRequest request) {
        return getGigaSpace(request.url, request.user, request.password);
    }

    @SuppressWarnings("deprecation")
    public static GigaSpace getGigaSpace(final String url, final String user, final String password) {
        UrlSpaceConfigurer urlSpaceConfigurer = new UrlSpaceConfigurer(url);
        urlSpaceConfigurer.userDetails(user, password);
        return new GigaSpaceConfigurer(urlSpaceConfigurer.create()).create();
    }

    public static Connection createJdbcConnection(final ExecuteRequest request)
            throws SQLException, ClassNotFoundException {
        Properties info = new Properties();

        if (request.user != null) {
            info.put("user", request.user);
        }
        if (request.password != null) {
            info.put("password", request.password);
        }

        return new GConnection(GConnection.JDBC_GIGASPACES_URL + request.url, info);
    }

}
