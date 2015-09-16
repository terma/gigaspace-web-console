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

package com.github.terma.gigaspacewebconsole.driver;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;
import com.github.terma.gigaspacewebconsole.core.GeneralRequest;
import com.j_spaces.jdbc.driver.GConnection;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;

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

    public static GConnection createJdbcConnection(final GeneralRequest request)
            throws SQLException, ClassNotFoundException {
        return createJdbcConnection(request.url, request.user, request.password);
    }

    public static GConnection createJdbcConnection(final String url, String user, String password)
            throws SQLException, ClassNotFoundException {
        Properties info = new Properties();

        if (user != null) {
            info.put("user", user);
        }
        if (password != null) {
            info.put("password", password);
        }

        return new GConnection(GConnection.JDBC_GIGASPACES_URL + url, info);
    }

    public static GConnection createJdbcConnection(final String url)
            throws SQLException, ClassNotFoundException {
        return createJdbcConnection(url, null, null);
    }

    public static void writeDocument(GigaSpace gigaSpace, String typeName, String property, Object value) {
        SpaceDocument spaceDocument1 = new SpaceDocument(typeName);
        spaceDocument1.setProperty(property, value);
        gigaSpace.write(spaceDocument1);
    }

    public static void writeDocument(
            GigaSpace gigaSpace, String typeName,
            String property1, Object value1, String property2, Object value2) {
        SpaceDocument spaceDocument1 = new SpaceDocument(typeName);
        spaceDocument1.setProperty(property1, value1);
        spaceDocument1.setProperty(property2, value2);
        gigaSpace.write(spaceDocument1);
    }

    public static SpaceDocument readByType(final GigaSpace gigaSpace, final String typeName) {
        return gigaSpace.read(new SpaceDocument(typeName));
    }
}
