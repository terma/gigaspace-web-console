package com.github.terma.gigaspacesqlconsole.provider;

import com.github.terma.gigaspacesqlconsole.core.CountsRequest;
import org.openspaces.admin.AdminFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

class AdminCache {

    private final Map<AdminCacheKey, AdminCacheItem> cache = new HashMap<>();
    private final long expirationTime;

    public AdminCache(final long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public AdminCache() {
        this(TimeUnit.MINUTES.toMillis(10));
    }

    public synchronized void clearExpired() {
        Iterator<Map.Entry<AdminCacheKey, AdminCacheItem>> iterator = cache.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<AdminCacheKey, AdminCacheItem> item = iterator.next();
            if (System.currentTimeMillis() - item.getValue().lastUsage > expirationTime) {
                iterator.remove();
                item.getValue().admin.close();
            }
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

    public synchronized int size() {
        return cache.size();
    }

    private static AdminCacheKey requestToKey(final CountsRequest request) {
        if (GigaSpaceUrl.isLocal(request.url)) {
            return new AdminCacheKey(null, request.user, request.password);
        } else {
            return new AdminCacheKey(GigaSpaceUrl.parseLocators(request.url), request.user, request.password);
        }
    }

}
