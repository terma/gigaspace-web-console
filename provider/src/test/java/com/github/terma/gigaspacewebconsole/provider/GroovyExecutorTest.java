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

package com.github.terma.gigaspacewebconsole.provider;

import com.github.terma.gigaspacewebconsole.core.ExecuteRequest;
import com.github.terma.gigaspacewebconsole.core.config.Config;
import com.github.terma.gigaspacewebconsole.provider.groovy.ObjectGroovyExecuteResponseStream;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class GroovyExecutorTest {

    private ObjectGroovyExecuteResponseStream responseStream = new ObjectGroovyExecuteResponseStream();
    private ExecuteRequest request = new ExecuteRequest();

    @BeforeClass
    public static void init() {
        System.setProperty(Config.CONFIG_PATH_SYSTEM_PROPERTY, Config.NONE);
    }

    @Before
    public void before() {
        request.url = "/./" + this.getClass().getName();
    }

    @Test
    public void shouldExecuteSimpleGroovy() throws Exception {
        request.sql = "1+1";

        GroovyExecutor.execute(request, responseStream);
    }

    @Test
    public void shouldExecuteMultiline() throws Exception {
        request.sql = "1+1\n1+2";

        GroovyExecutor.execute(request, responseStream);
    }

    @Test
    public void shouldPrintLastResultByDefault() throws Exception {
        request.sql = "\"fff\"";

        GroovyExecutor.execute(request, responseStream);

        assertEquals(1, responseStream.results.size());
        assertEquals("fff", responseStream.results.get(0).data.get(0).get(0));
    }

    @Test
    public void shouldNotPrintByDefaultIfLastNotResult() throws Exception {
        request.sql = "";

        GroovyExecutor.execute(request, responseStream);

        assertEquals(0, responseStream.results.size());
    }

    @Test(expected = Exception.class)
    public void shouldHandleException() throws Exception {
        request.sql = "1/0";

        GroovyExecutor.execute(request, responseStream);
    }

    @Test
    public void shouldAllowPrintFromScript() throws Exception {
        request.sql = "out 12";

        GroovyExecutor.execute(request, responseStream);

        assertEquals(1, responseStream.results.size());
    }

    @Test
    public void shouldAllowPrintMultiFromScript() throws Exception {
        request.sql = "out 12, 'aaa'";

        GroovyExecutor.execute(request, responseStream);

        assertEquals(1, responseStream.results.size());
    }

    @Test
    public void shouldAllowToUseGsInstanceInScript() throws Exception {
        request.sql = "driver.clear(null)";

        GroovyExecutor.execute(request, responseStream);
    }

    @Test
    public void shouldAllowToExecuteSql() throws Exception {
        request.sql = "sql 'create table Customer (id int)'";

        GroovyExecutor.execute(request, responseStream);


        assertEquals(1, responseStream.results.size());
    }

    @Test
    public void shouldAllowToGetSpaceMemUsage() throws Exception {
        request.sql = "mem()";

        GroovyExecutor.execute(request, responseStream);

        assertEquals(1, responseStream.results.size());
    }

    @Test
    public void shouldAllowToGetAdmin() throws Exception {
        request.sql = "admin().spaces.spaces.length";

        GroovyExecutor.execute(request, responseStream);

        assertEquals(1, responseStream.results.size());
    }

    @Test
    public void haveDefaultGsDocumentImport() throws Exception {
        request.sql = "new SpaceDocument(\"bb\")";

        GroovyExecutor.execute(request, responseStream);

        assertEquals(1, responseStream.results.size());
    }

    @Test
    public void haveDefaultGsSqlQueryImport() throws Exception {
        request.sql = "new SQLQuery(\"bb\", \"a\")";

        GroovyExecutor.execute(request, responseStream);

        assertEquals(1, responseStream.results.size());
    }

}
