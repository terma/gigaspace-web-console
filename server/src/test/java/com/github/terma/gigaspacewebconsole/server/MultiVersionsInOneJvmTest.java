package com.github.terma.gigaspacewebconsole.server;

import com.github.terma.gigaspacewebconsole.core.ExecuteRequest;
import com.github.terma.gigaspacewebconsole.core.GeneralRequest;
import com.github.terma.gigaspacewebconsole.core.ObjectExecuteResponseStream;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Checking that we can work with different versions of GS in
 * same JVM in one thread
 */
@Ignore
public class MultiVersionsInOneJvmTest {

    private static final int NATURAL_DELAY = 50;

    @Test
    public void shouldExecuteWithoutErrors() throws Exception {
        ExecuteRequest request1 = new ExecuteRequest();
        request1.url = "/./gs95-" + System.currentTimeMillis();
        request1.sql = "create table gs95 (id int)";

        GeneralRequest countsRequest1 = new GeneralRequest();
        countsRequest1.url = request1.url + "?";

        ExecuteRequest request2 = new ExecuteRequest();
        request2.url = "/./gs10-" + System.currentTimeMillis();
        request2.sql = "create table gs10 (id int)";

        GeneralRequest countsRequest2 = new GeneralRequest();
        countsRequest2.url = request2.url + "?";

        for (int i = 0; i < 15; i++) {
            CachedProviderResolver.getProvider("GS-9.5").query(request1, new ObjectExecuteResponseStream());
            Thread.sleep(NATURAL_DELAY);
            CachedProviderResolver.getProvider("GS-9.5").counts(countsRequest1);
            Thread.sleep(NATURAL_DELAY);
            CachedProviderResolver.getProvider("GS-10").query(request2, new ObjectExecuteResponseStream());
            Thread.sleep(NATURAL_DELAY);
            CachedProviderResolver.getProvider("GS-10").counts(countsRequest2);
            Thread.sleep(NATURAL_DELAY);
        }
    }

}
