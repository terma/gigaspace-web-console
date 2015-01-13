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

public class Counts {

    //    case class CacheItem(admin: Admin, space: Space, var lastUsage: Long)
    private static class CacheItem {

        public Admin admin;
        public Space space;
        public long lastUsage;

    }

    private static final Map<CountsRequest, CacheItem> cache = new HashMap<>();

    private static final Thread cleaner = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                clearExpired();

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

    private synchronized static void clearExpired() {
        for (final Map.Entry<CountsRequest, CacheItem> cacheItem : cache.entrySet()) {
            if (System.currentTimeMillis() - cacheItem.getValue().lastUsage > TimeUnit.MINUTES.toMillis(10)) {
                cache.remove(cacheItem.getKey());
                cacheItem.getValue().admin.close();
            }
        }
    }

    public synchronized static void clear() {
        System.out.println("Start clear...");

        for (final Map.Entry<CountsRequest, CacheItem> item : cache.entrySet()) {
            System.out.println("Start clear...");
            item.getValue().admin.close();
        }
    }

    private synchronized static CacheItem createOrGetAdmin(CountsRequest request) {
        CacheItem item = cache.get(request);
        if (item == null) {
            final AdminFactory adminFactory = new AdminFactory();
            adminFactory.useDaemonThreads(true);
            // reduce amount of info which admin collects
            adminFactory.setDiscoveryServices(Space.class);

            if (GigaSpaceUrl.isLocal(request.url)) {
                adminFactory.discoverUnmanagedSpaces();
            } else {
                adminFactory.credentials(request.user, request.password);
                final String locator = GigaSpaceUrl.parseLocator(request.url);
                System.out.println("Starting to get admin for " + locator + "...");
                adminFactory.addLocator(locator);
            }

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
        } else {
            System.out.println("Use cached admin for " + request.url);
        }

        // update last usage
        item.lastUsage = System.currentTimeMillis();

        return item;
    }

    public static CountsResponse counts(CountsRequest request) {
        if (request.url.equals("/./test")) {
            return createTestResponse();
        }

        final CacheItem adminAndSpace = createOrGetAdmin(request);

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
