package com.github.terma.gigaspacesqlconsole;

import com.gigaspaces.cluster.activeelection.SpaceMode;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.space.Space;
import org.openspaces.admin.space.SpaceInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Counts {

    //    case class CacheItem(admin: Admin, space: Space, var lastUsage: Long)
    private static class CacheItem {

        public Admin admin;
        public Space space;
        public long lastUsage;

    }

    private static final ReentrantLock lock = new ReentrantLock();
    private static final Map<CountsRequest, CacheItem> cache = new HashMap<>();

    private static final Thread cleaner = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                lock.lock();
                try {
                    for (final Map.Entry<CountsRequest, CacheItem> cacheItem : cache.entrySet()) {
                        if (System.currentTimeMillis() - cacheItem.getValue().lastUsage > TimeUnit.MINUTES.toMillis(10)) {
                            cache.remove(cacheItem.getKey());
                            cacheItem.getValue().admin.close();
                        }
                    }
                } finally {
                    lock.unlock();
                }

                try {
                    Thread.sleep(TimeUnit.MINUTES.toMillis(5));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

    });

    static {
        cleaner.setName("GS-COUNTS-CLEANER");
        cleaner.setDaemon(true);
        cleaner.start();
    }

    public static void clear() {
        lock.lock();
        try {
            System.out.println("Start clear...");

            for (final Map.Entry<CountsRequest, CacheItem> item : cache.entrySet()) {
                System.out.println("Start clear...");
                item.getValue().admin.close();
            }
        } finally {
            lock.unlock();
        }
    }

    private static CacheItem createOrGetAdmin(CountsRequest request) {
        System.out.println("Waiting lock for " + request.hashCode());
        lock.lock();
        System.out.println("Get lock for " + request.hashCode());
        try {
            CacheItem item = cache.get(request);
            if (item == null) {
                final String locator = GigaSpaceUrl.parseLocator(request.url);
                System.out.println("Starting to get admin for " + locator + "...");
                final AdminFactory adminFactory = new AdminFactory();
                adminFactory.addLocator(locator);
                adminFactory.credentials(request.user, request.password);
                final Admin admin = adminFactory.createAdmin();

                final String spaceName = GigaSpaceUrl.parseSpace(request.url);
                System.out.println("Trying connect to space " + spaceName + "...");
                Space space = admin.getSpaces().waitFor(spaceName, 20, TimeUnit.SECONDS);
                if (space == null) {
                    admin.close();
                    throw new IllegalArgumentException("Can't find space with url: " + request.url);
                }

                System.out.println("connected to space!");
                item = new CacheItem();
                item.admin = admin;
                item.space = space;
                cache.put(request, item);
            }

            // update last usage
            item.lastUsage = System.currentTimeMillis();

            return item;
        } finally {
            lock.unlock();
        }
    }

    public static CountsResponse counts(CountsRequest request) {
        if (request.url.equals("/./test")) {
            return createTestResponse();
        }

        final CacheItem adminAndSpace = createOrGetAdmin(request);

        SpaceInstance[] spaceInstances = adminAndSpace.space.getInstances();

        Map<String, Integer> counts = new HashMap<>();

        for (final SpaceInstance spaceInstance : spaceInstances) {
            if (spaceInstance.getMode() == SpaceMode.PRIMARY) {
                for (final Map.Entry<String, Integer> countItem : instanceToCounts(spaceInstance).entrySet()) {
                    int count = counts.getOrDefault(countItem.getKey(), 0);
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
        Count count1 = new Count();
        count1.name = "com.github.terma.gigaspacesqlconsole.TestType";
        count1.count = new Random().nextInt(10);
        countsResponse.counts.add(count1);
        Count count2 = new Count();
        count2.name = "com.github.terma.gigaspacesqlconsole.Momo";
        count2.count = 1;
        countsResponse.counts.add(count2);
        return countsResponse;
    }

    private static Map<String, Integer> instanceToCounts(SpaceInstance instance) {
        if (instance != null) {
            return instance.getRuntimeDetails().getCountPerClassName();
        } else {
            return new HashMap<>();
        }
    }

}
