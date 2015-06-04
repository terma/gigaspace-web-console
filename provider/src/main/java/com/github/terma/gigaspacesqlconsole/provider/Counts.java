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

import com.gigaspaces.cluster.activeelection.SpaceMode;
import com.github.terma.gigaspacesqlconsole.core.Count;
import com.github.terma.gigaspacesqlconsole.core.CountsResponse;
import com.github.terma.gigaspacesqlconsole.core.GeneralRequest;
import org.openspaces.admin.space.Space;
import org.openspaces.admin.space.SpaceInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Counts {

    private static final Logger LOGGER = Logger.getLogger(Counts.class.getName());

    public CountsResponse counts(GeneralRequest request) {
        if (request.url.equals("/./test")) {
            return createTestResponse();
        }

        final CountsResponse countsResponse = new CountsResponse();
        countsResponse.counts = new ArrayList<>();

        final AdminCacheItem adminAndSpace = AdminService.get(request);

        Space space = adminAndSpace.admin.getSpaces()
                .waitFor(GigaSpaceUrl.parseSpace(request.url), 10, TimeUnit.SECONDS);
        if (space != null) {
            SpaceInstance[] spaceInstances = space.getInstances();
            LOGGER.fine("Admin has " + spaceInstances.length + " space instances");

            final Map<String, Integer> counts = new HashMap<>();
            for (final SpaceInstance spaceInstance : spaceInstances) {
                if (spaceInstance.getMode() != SpaceMode.BACKUP) {
                    for (final Map.Entry<String, Integer> countItem : instanceToCounts(spaceInstance).entrySet()) {
                        LOGGER.fine("Space instance " + spaceInstance + " has " + countItem + " types");
                        Integer count = counts.get(countItem.getKey());
                        if (count == null) count = 0;
                        counts.put(countItem.getKey(), count + countItem.getValue());
                    }
                }
            }

            for (final Map.Entry<String, Integer> count : counts.entrySet()) {
                final Count countResponse = new Count();
                countResponse.name = count.getKey();
                countResponse.count = count.getValue();
                countsResponse.counts.add(countResponse);
            }
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
