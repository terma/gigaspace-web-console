package com.github.terma.gigaspacesqlconsole.provider;

import com.github.terma.gigaspacesqlconsole.core.CountsRequest;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.space.Space;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

class AdminCache {

    private final Map<CountsRequest, AdminAndSpaceCacheItem> cache = new HashMap<>();

    public synchronized void clearExpired() {
        for (final Map.Entry<CountsRequest, AdminAndSpaceCacheItem> cacheItem : cache.entrySet()) {
            if (System.currentTimeMillis() - cacheItem.getValue().lastUsage > TimeUnit.MINUTES.toMillis(10)) {
                cache.remove(cacheItem.getKey());
                cacheItem.getValue().admin.close();
            }
        }
    }

    public synchronized void clear() {
        System.out.println("Start clear...");

        for (final Map.Entry<CountsRequest, AdminAndSpaceCacheItem> item : cache.entrySet()) {
            System.out.println("Start clear...");
            item.getValue().admin.close();
        }
    }

    public synchronized AdminAndSpaceCacheItem createOrGet(final CountsRequest request) {
        AdminAndSpaceCacheItem item = cache.get(request);
        if (item == null) {
            final AdminFactory adminFactory = new AdminFactory();
            adminFactory.useDaemonThreads(true);
            // reduce amount of info which admin collects
//            adminFactory.setDiscoveryServices(Space.class);

            if (GigaSpaceUrl.isLocal(request.url)) {
                adminFactory.discoverUnmanagedSpaces();
            } else {
                adminFactory.credentials(request.user, request.password);
                final String locators = GigaSpaceUrl.parseLocators(request.url);
                System.out.println("Starting to get admin for " + locators + "...");
                adminFactory.addLocators(locators);
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
            item = new AdminAndSpaceCacheItem();
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

}
