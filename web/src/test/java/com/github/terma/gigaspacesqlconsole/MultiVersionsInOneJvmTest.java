package com.github.terma.gigaspacesqlconsole;

import com.github.terma.gigaspacesqlconsole.core.ExecuteRequest;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This test show that we work with different version of GS in same JVM
 */
@Ignore
public class MultiVersionsInOneJvmTest {

    @Test
    public void shouldExecuteWithoutErrors() throws InterruptedException {
        MyRunnable thread2 = new MyRunnable("GS-9.5");
        MyRunnable thread1 = new MyRunnable("GS-10");

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        Assert.assertEquals(null, thread1.exception);
        Assert.assertEquals(null, thread2.exception);
    }

    private static class MyRunnable extends Thread {

        private final String gs;

        public volatile Exception exception;

        public MyRunnable(String gs) {
            this.gs = gs;
            exception = null;
        }

        @Override
        public void run() {
            ExecuteRequest request = new ExecuteRequest();
            request.url = "/./test" + System.currentTimeMillis();
            request.sql = "create table TEST (ID int)";
            for (int i = 0; i < 15; i++) {
                try {
                    CachedProviderResolver.getProvider(gs).query(request);
                    Thread.sleep(100);
                } catch (Exception e) {
                    exception = e;
//                    e.printStackTrace();
                }
            }
        }

    }
}
