package com.github.terma.gigaspacesqlconsole.provider;

import com.gigaspaces.cluster.activeelection.SpaceMode;
import com.github.terma.gigaspacesqlconsole.core.Count;
import com.github.terma.gigaspacesqlconsole.core.CountsRequest;
import com.github.terma.gigaspacesqlconsole.core.CountsResponse;
import org.openspaces.admin.space.SpaceInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class CountsProviderImpl {

    private static final AdminCache adminCache = new AdminCache();

    private static final Thread cleaner = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                adminCache.clearExpired();

                try {
                    Thread.sleep(TimeUnit.MINUTES.toMillis(5));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

    });

    static {
        cleaner.setName("COUNTS-ADMIN-CLEANER");
        cleaner.setDaemon(true);
        cleaner.start();
    }

    public CountsResponse counts(CountsRequest request) {
        if (request.url.equals("/./test")) {
            return createTestResponse();
        }

        final AdminAndSpaceCacheItem adminAndSpace = adminCache.createOrGet(request);

        SpaceInstance[] spaceInstances = adminAndSpace.space.getInstances();
        System.out.println("Admin has " + spaceInstances.length + " space instances");

        final Map<String, Integer> counts = new HashMap<>();
        for (final SpaceInstance spaceInstance : spaceInstances) {
            if (spaceInstance.getMode() != SpaceMode.BACKUP) {
                for (final Map.Entry<String, Integer> countItem : instanceToCounts(spaceInstance).entrySet()) {
                    System.out.println("Space instance " + spaceInstance + " has " + countItem + " types");
                    Integer count = counts.get(countItem.getKey());
                    if (count == null) count = 0;
                    counts.put(countItem.getKey(), count + countItem.getValue());
                }
            }
        }

        final CountsResponse countsResponse = new CountsResponse();
        countsResponse.counts = new ArrayList<>();
        for (final Map.Entry<String, Integer> count : counts.entrySet()) {
            final Count countResponse = new Count();
            countResponse.name = count.getKey();
            countResponse.count = count.getValue();
            countsResponse.counts.add(countResponse);
        }
        return countsResponse;
    }

    private static CountsResponse createTestResponse() {
        CountsResponse countsResponse = new CountsResponse();
        countsResponse.counts = new ArrayList<>();

        Count count3 = new Count();
        count3.name = "com.github.terma.gigaspacesqlconsole.ZT";
        count3.count = new Random().nextInt(100);
        countsResponse.counts.add(count3);

        Count count4 = new Count();
        count4.name = "com.github.terma.gigaspacesqlconsole.AT";
        count4.count = new Random().nextInt(1000);
        countsResponse.counts.add(count4);

        Count count5 = new Count();
        count5.name = "com.github.terma.gigaspacesqlconsole.Time";
        count5.count = (int) (System.currentTimeMillis() / 1000);
        countsResponse.counts.add(count5);

        Count count7 = new Count();
        count7.name = "com.github.terma.gigaspacesqlconsole.Zero";
        count7.count = 0;
        countsResponse.counts.add(count7);

        Count count1 = new Count();
        count1.name = "com.github.terma.gigaspacesqlconsole.TestType";
        count1.count = new Random().nextInt(10);
        countsResponse.counts.add(count1);

        Count count2 = new Count();
        count2.name = "com.github.terma.gigaspacesqlconsole.Momo";
        count2.count = 1;
        countsResponse.counts.add(count2);

        if (new Random().nextBoolean()) {
            Count count6 = new Count();
            count6.name = "com.github.terma.gigaspacesqlconsole.Temp";
            count6.count = 1;
            countsResponse.counts.add(count6);
        }

        return countsResponse;
    }

    private static Map<String, Integer> instanceToCounts(final SpaceInstance instance) {
        if (instance != null) {
            return instance.getRuntimeDetails().getCountPerClassName();
        } else {
            return new HashMap<>();
        }
    }

}
