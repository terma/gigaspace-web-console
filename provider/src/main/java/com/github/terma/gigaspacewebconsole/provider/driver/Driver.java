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

package com.github.terma.gigaspacewebconsole.provider.driver;

import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Driver implements java.sql.Driver {

    static final String JDBC_PREFIX = "jdbc:com.github.terma.gigaspacewebconsole:";

    private static final Logger LOGGER = Logger.getLogger(Driver.class.getPackage().getName());

    static {
        try {
            DriverManager.registerDriver(new Driver());
        } catch (SQLException exception) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, exception.getMessage(), exception);
            }
        }
    }

    @Override
    public int getMajorVersion() {
        return 1;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        if (url == null) throw new SQLException("Url can't be null!");
        return url.startsWith(JDBC_PREFIX);
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        return this.acceptsURL(url) ? new Connection(url, info) : null;
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return null;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return LOGGER;
    }

}
