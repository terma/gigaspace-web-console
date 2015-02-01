package com.github.terma.gigaspacesqlconsole.provider;

import com.github.terma.gigaspacesqlconsole.core.CountsRequest;
import com.github.terma.gigaspacesqlconsole.core.CountsResponse;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class CountsStabilityTest {

    @Test
    public void shouldWorkCorrectForMultiClients() throws InterruptedException, ExecutionException {
        final List<String> spaces = new ArrayList<String>() {{
            add("/./space1?");
            add("/./space2?");
            add("/./space3?");
        }};

        for (final String space : spaces)
            GigaSpaceUtils.getGigaSpace(space);

        ExecutorService executorService = Executors.newFixedThreadPool(20);

        final List<Callable<Void>> tasks = new ArrayList<>();

        final Random random = new Random();
        for (int i = 0; i < 10000; i++)
            tasks.add(new ClientRequest(spaces.get(random.nextInt(spaces.size()))));

        final List<Future<Void>> results = executorService.invokeAll(tasks);

        int r = 0;
        for (final Future<Void> result : results) {
            Assert.assertNull(result.get());
            r++;
        }

        System.out.println(r);
//        executorService.awaitTermination(30, TimeUnit.SECONDS);
    }

    private static class ClientRequest implements Callable<Void> {

        private final String space;

        private ClientRequest(String space) {
            this.space = space;
        }

        @Override
        public Void call() {
            CountsRequest countsRequest = new CountsRequest();
            countsRequest.url = space;
            final CountsResponse countsResponse = new Counts().counts(countsRequest);
            Assert.assertEquals(1, countsResponse.counts.size());
            return null;
        }

    }

}
