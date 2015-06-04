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

import com.github.terma.gigaspacesqlconsole.core.ExecuteRequest;
import com.github.terma.gigaspacesqlconsole.core.GeneralRequest;
import org.openspaces.admin.Admin;

import java.util.concurrent.TimeUnit;

public class AdminService {

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
                    return;
                }
            }
        }

    });

    static {
        cleaner.setName("ADMIN-CLEANER");
        cleaner.setDaemon(true);
        cleaner.start();
    }

    public static Admin get(GeneralRequest request) {
        return adminCache.createOrGet(request).admin;
    }

}
