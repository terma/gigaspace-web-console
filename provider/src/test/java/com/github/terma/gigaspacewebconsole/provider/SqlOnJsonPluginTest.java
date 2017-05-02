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

package com.github.terma.gigaspacewebconsole.provider;

import com.github.terma.gigaspacewebconsole.core.ExecuteRequest;
import com.github.terma.gigaspacewebconsole.core.ExecuteResponseStream;
import com.github.terma.gigaspacewebconsole.provider.executor.ConnectionFactory;
import com.github.terma.gigaspacewebconsole.provider.executor.SqlOnJsonPlugin;
import junit.framework.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static java.util.Collections.singletonList;

public class SqlOnJsonPluginTest {

    private ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
    private Connection connection = Mockito.mock(Connection.class);
    private PreparedStatement ps = Mockito.mock(PreparedStatement.class);
    private ResultSet rs = Mockito.mock(ResultSet.class);
    private ExecuteResponseStream executeResponseStream = Mockito.mock(ExecuteResponseStream.class);
    private ConverterHelper converterHelper = Mockito.mock(ConverterHelper.class);

    @Test
    public void shouldNotHandleNonSqlOnJsonRequests() throws Exception {
        ExecuteRequest executeRequest = new ExecuteRequest();
        executeRequest.sql = "select ";
        boolean r = new SqlOnJsonPlugin(null, null)
                .execute(executeRequest, null);
        Assert.assertFalse(r);
    }

    @Test
    public void shouldThrowExceptionWhenNoRowsWithJson() throws Exception {
        Mockito.when(connectionFactory.get(Mockito.<ExecuteRequest>any())).thenReturn(connection);
        Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(ps);
        Mockito.when(ps.executeQuery()).thenReturn(rs);

        ExecuteRequest executeRequest = new ExecuteRequest();
        executeRequest.sql = "sql_on_json(select (s) from b) select * from d";
        try {
            new SqlOnJsonPlugin(connectionFactory, null)
                    .execute(executeRequest, null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("No row with JSON for sql_on_json(select (s) from b) select * from d", e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionWhenNoNullJson() throws Exception {
        Mockito.when(connectionFactory.get(Mockito.<ExecuteRequest>any())).thenReturn(connection);
        Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(ps);
        Mockito.when(ps.executeQuery()).thenReturn(rs);
        Mockito.when(rs.next()).thenReturn(true);

        ExecuteRequest executeRequest = new ExecuteRequest();
        executeRequest.sql = "sql_on_json(select (s) from b) select * from d";
        try {
            new SqlOnJsonPlugin(connectionFactory, null)
                    .execute(executeRequest, null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Null JSON for sql_on_json(select (s) from b) select * from d", e.getMessage());
        }
    }

    @Test
    public void shouldWork() throws Exception {
        Mockito.when(connectionFactory.get(Mockito.<ExecuteRequest>any())).thenReturn(connection);
        Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(ps);
        Mockito.when(ps.executeQuery()).thenReturn(rs);
        Mockito.when(rs.next()).thenReturn(true);
        Mockito.when(rs.getString(1)).thenReturn("{d:[{id:1}]}");
        Mockito.when(converterHelper.getFormattedValue(Mockito.<ResultSet>any(), Mockito.anyString())).thenReturn("???");

        ExecuteRequest executeRequest = new ExecuteRequest();
        executeRequest.sql = "sql_on_json(select (s) from b) select * from d";
        boolean r = new SqlOnJsonPlugin(connectionFactory, converterHelper)
                .execute(executeRequest, executeResponseStream);

        Assert.assertTrue(r);
        Mockito.verify(connection).prepareStatement("select (s) from b");
        Mockito.verify(executeResponseStream).writeHeader(singletonList("ID"));
        Mockito.verify(executeResponseStream).writeRow(singletonList("???"), singletonList("java.lang.Long"));
    }

}
