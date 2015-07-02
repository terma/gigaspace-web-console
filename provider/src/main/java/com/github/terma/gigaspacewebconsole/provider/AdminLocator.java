package com.github.terma.gigaspacewebconsole.provider;

import com.github.terma.gigaspacewebconsole.core.GeneralRequest;
import org.openspaces.admin.Admin;

import java.util.concurrent.TimeUnit;

public class AdminLocator {

    private static final AdminCache CACHE = new AdminCache();

    private static final Thread CLEANER = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                CACHE.clearExpired();

                try {
                    Thread.sleep(TimeUnit.MINUTES.toMillis(5));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

    });

    static {
        CLEANER.setName("ADMIN-CACHE-CLEANER");
        CLEANER.setDaemon(true);
        CLEANER.start();
    }

    public static Admin get(GeneralRequest request) {
        return CACHE.createOrGet(request).admin;
    }

}
