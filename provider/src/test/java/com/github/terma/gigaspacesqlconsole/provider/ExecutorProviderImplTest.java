package com.github.terma.gigaspacesqlconsole.provider;

import com.github.terma.gigaspacesqlconsole.core.ExecuteRequest;
import junit.framework.Assert;
import org.junit.Test;

import java.sql.SQLException;

public class ExecutorProviderImplTest {

    @Test
    public void shouldGetConnection() throws SQLException, ClassNotFoundException {
        ExecuteRequest request = new ExecuteRequest();
        request.url = "/./test-gs10-space";

        Assert.assertNotNull(new ExecutorProviderImpl().getConnection(request));
    }

}
