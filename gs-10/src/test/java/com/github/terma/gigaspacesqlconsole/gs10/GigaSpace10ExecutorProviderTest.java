package com.github.terma.gigaspacesqlconsole.gs10;

import com.github.terma.gigaspacesqlconsole.ExecuteRequest;
import junit.framework.Assert;
import org.junit.Test;

import java.sql.SQLException;

public class GigaSpace10ExecutorProviderTest {

    @Test
    public void shouldGetConnection() throws SQLException, ClassNotFoundException {
        ExecuteRequest request = new ExecuteRequest();
        request.url = "/./test-gs10-space";

        Assert.assertNotNull(new GigaSpace10ExecutorProvider().getConnection(request));
    }

}
