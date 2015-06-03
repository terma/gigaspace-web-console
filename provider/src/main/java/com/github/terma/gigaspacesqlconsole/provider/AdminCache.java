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

package com.github.terma.gigaspacesqlconsole.provider;

import com.github.terma.gigaspacesqlconsole.core.CountsRequest;
import org.openspaces.admin.AdminFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

class AdminCache {

    private static final Logger LOGGER = Logger.getLogger(AdminCache.class.getName());

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
                LOGGER.fine("Close expired admin " + item.getKey() + "...");
                iterator.remove();
                item.getValue().admin.close();
                LOGGER.fine("Expired admin closed");
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

            LOGGER.fine("Creating admin for " + key + "...");
            if (key.locators == null) {
                adminFactory.discoverUnmanagedSpaces();
            } else {
                adminFactory.userDetails(request.user, request.password);
                adminFactory.addLocators(key.locators);
            }

            item = new AdminCacheItem();
            item.admin = adminFactory.createAdmin();
            cache.put(key, item);
        } else {
            LOGGER.fine("Use cached admin for " + request.url);
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
