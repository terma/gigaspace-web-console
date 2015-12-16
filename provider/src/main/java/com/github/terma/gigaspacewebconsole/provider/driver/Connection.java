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

package com.github.terma.gigaspacewebconsole.provider.driver;

import com.gigaspaces.internal.client.spaceproxy.ISpaceProxy;
import com.j_spaces.jdbc.driver.GConnection;

import java.lang.reflect.Field;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;

public class Connection extends GConnection {

    public Connection(String url, Properties properties) throws SQLException {
        super(convertUrlToGsUrl(url), properties);
    }

    public Connection(String url) throws SQLException {
        this(url, new Properties());
    }

    private static String convertUrlToGsUrl(String url) {
        if (!url.startsWith(Driver.JDBC_PREFIX)) {
            throw new IllegalArgumentException("Unrecognized url to converting to gs url!");
        }
        return GConnection.JDBC_GIGASPACES_URL + url.substring(Driver.JDBC_PREFIX.length());
    }

    @Override
    public DatabaseMetaData getMetaData() {
        return new FixedMetaData(this);
    }

    ISpaceProxy getSpace() {
        final Class<?> gConnectionClass = GConnection.class;
        try {
            Field spaceField = gConnectionClass.getDeclaredField("space");
            spaceField.setAccessible(true);

            return (ISpaceProxy) spaceField.get(this);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Can't access to space field in connection!\n" +
                    "Connection class: " + this + "\n" +
                    "Declared fields: " + Arrays.asList(gConnectionClass.getDeclaredFields()),
                    e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
