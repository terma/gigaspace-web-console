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

import junit.framework.Assert;
import org.junit.Test;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionTest {

    @Test
    public void shouldHaveGoodMetaData() throws SQLException {
        Connection connection = new Connection("jdbc:com.github.terma.gigaspacewebconsole:/./connection", new Properties());
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        Assert.assertTrue(databaseMetaData instanceof FixedMetaData);
    }

}
