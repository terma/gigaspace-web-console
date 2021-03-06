package com.github.terma.gigaspacewebconsole.provider.executor.database;

import com.github.terma.gigaspacewebconsole.core.ExecuteRequest;
import com.github.terma.gigaspacewebconsole.core.ObjectExecuteResponseStream;
import com.github.terma.gigaspacewebconsole.core.ObjectRow;
import com.github.terma.gigaspacewebconsole.core.config.ConfigFactory;
import com.github.terma.gigaspacewebconsole.provider.executor.DatabaseExecutor;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static java.util.Collections.singletonList;

public class DatabaseExecutorTest {

    private ExecuteRequest request;

    @BeforeClass
    public static void before() {
        System.setProperty(ConfigFactory.CONFIG_PATH_SYSTEM_PROPERTY, ConfigFactory.NONE);
    }

    @Before
    public void createRequest() {
        request = new ExecuteRequest();
        request.url = "jdbc:h2:mem:execute" + Math.random() + ";DB_CLOSE_DELAY=-1";
        request.driver = "org.h2.Driver";
    }

    @Test
    public void shouldAllowExecute() throws Exception {
        request.sql = "create table A (id int);";

        ObjectExecuteResponseStream responseStream = new ObjectExecuteResponseStream();
        DatabaseExecutor.INSTANCE.execute(request, responseStream);

        Assert.assertEquals(1, responseStream.getColumns().size());
        Assert.assertEquals(1, responseStream.getData().size());
    }

    @Test
    public void shouldAllowQuery() throws Exception {
        request.sql = "create table A (id int);";
        DatabaseExecutor.INSTANCE.execute(request, new ObjectExecuteResponseStream());

        request.sql = "insert into A values (12);";
        DatabaseExecutor.INSTANCE.execute(request, new ObjectExecuteResponseStream());

        request.sql = "select * from A";
        ObjectExecuteResponseStream responseStream = new ObjectExecuteResponseStream();
        DatabaseExecutor.INSTANCE.execute(request, responseStream);

        Assert.assertEquals(1, responseStream.getColumns().size());
        Assert.assertEquals(1, responseStream.getData().size());
        Assert.assertEquals(new ObjectRow(singletonList("12"), singletonList("java.lang.Integer")), responseStream.getData().get(0));
    }

    @Test
    public void shouldSupportMultipleQueries() throws Exception {
        request.sql = "create table A (id int);\nselect * from A;";

        ObjectExecuteResponseStream responseStream = new ObjectExecuteResponseStream();
        DatabaseExecutor.INSTANCE.execute(request, responseStream);

        Assert.assertEquals(1, responseStream.getColumns().size());
        Assert.assertEquals(1, responseStream.getData().size());
        Assert.assertEquals(new ObjectRow(singletonList("0"), singletonList("java.lang.Integer")), responseStream.getData().get(0));
    }

}
