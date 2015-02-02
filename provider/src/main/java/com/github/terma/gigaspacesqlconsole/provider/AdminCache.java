package com.github.terma.gigaspacesqlconsole.provider;

import com.github.terma.gigaspacesqlconsole.core.CountsRequest;
import org.openspaces.admin.AdminFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

class AdminCache {

    private final Map<AdminCacheKey, AdminCacheItem> cache = new HashMap<>();

    public synchronized void clearExpired() {
        for (final Map.Entry<AdminCacheKey, AdminCacheItem> cacheItem : cache.entrySet()) {
            if (System.currentTimeMillis() - cacheItem.getValue().lastUsage > TimeUnit.MINUTES.toMillis(10)) {
                cache.remove(cacheItem.getKey());
                cacheItem.getValue().admin.close();
            }
        }
    }

    public synchronized void clear() {
        System.out.println("Start clear...");

        // todo add map clear
        for (final Map.Entry<AdminCacheKey, AdminCacheItem> item : cache.entrySet()) {
            System.out.println("Start clear...");
            item.getValue().admin.close();
        }
    }

    public synchronized AdminCacheItem createOrGet(final CountsRequest request) {
        final AdminCacheKey key = requestToKey(request);

        AdminCacheItem item = cache.get(key);
        if (item == null) {
            final AdminFactory adminFactory = new AdminFactory();
            adminFactory.useDaemonThreads(true);
            // reduce amount of info which admin collects
//            adminFactory.setDiscoveryServices(Space.class);

            if (key.locators == null) {
                adminFactory.discoverUnmanagedSpaces();
            } else {
                adminFactory.userDetails(request.user, request.password);
                System.out.println("Starting to get admin for " + key.locators + "...");
                adminFactory.addLocators(key.locators);
            }

            item = new AdminCacheItem();
            item.admin = adminFactory.createAdmin();
            cache.put(key, item);
        } else {
            System.out.println("Use cached admin for " + request.url);
        }

        // update last usage
        item.lastUsage = System.currentTimeMillis();

        return item;
    }

    private static AdminCacheKey requestToKey(final CountsRequest request) {
        if (GigaSpaceUrl.isLocal(request.url)) {
            return new AdminCacheKey(null, request.user, request.password);
        } else {
            return new AdminCacheKey(GigaSpaceUrl.parseLocators(request.url), request.user, request.password);
        }
    }

}
