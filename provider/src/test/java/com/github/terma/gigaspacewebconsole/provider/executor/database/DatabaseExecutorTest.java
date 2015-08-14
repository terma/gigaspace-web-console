package com.github.terma.gigaspacewebconsole.provider.executor.database;

import com.github.terma.gigaspacewebconsole.core.ExecuteRequest;
import com.github.terma.gigaspacewebconsole.core.ObjectExecuteResponseStream;
import com.github.terma.gigaspacewebconsole.core.config.Config;
import com.github.terma.gigaspacewebconsole.provider.executor.DatabaseExecutor;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;

public class DatabaseExecutorTest {

    @BeforeClass
    public static void before() {
        System.setProperty(Config.CONFIG_PATH_SYSTEM_PROPERTY, Config.NONE);
    }

    @Test
    public void shouldAllowExecute() throws Exception {
        ExecuteRequest request = new ExecuteRequest();
        request.url = "jdbc:h2:mem:execute";
        request.driver = "org.h2.Driver";
        request.sql = "create table A (id int);";

        ObjectExecuteResponseStream responseStream = new ObjectExecuteResponseStream();
        DatabaseExecutor.INSTANCE.execute(request, responseStream);

        Assert.assertEquals(1, responseStream.getColumns().size());
        Assert.assertEquals(1, responseStream.getData().size());
    }

    @Test
    public void shouldAllowQuery() throws Exception {
        ExecuteRequest request = new ExecuteRequest();
        request.url = "jdbc:h2:mem:query;DB_CLOSE_DELAY=-1";
        request.driver = "org.h2.Driver";

        request.sql = "create table A (id int);";
        DatabaseExecutor.INSTANCE.execute(request, new ObjectExecuteResponseStream());

        request.sql = "insert into A values (12);";
        DatabaseExecutor.INSTANCE.execute(request, new ObjectExecuteResponseStream());

        request.sql = "select * from A";
        ObjectExecuteResponseStream responseStream = new ObjectExecuteResponseStream();
        DatabaseExecutor.INSTANCE.execute(request, responseStream);

        Assert.assertEquals(1, responseStream.getColumns().size());
        Assert.assertEquals(1, responseStream.getData().size());
        Assert.assertEquals(Collections.singletonList("12"), responseStream.getData().get(0));
    }

}
