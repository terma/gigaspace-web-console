/*
Copyright 2015-2017 Artem Stasiuk

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

package com.github.terma.gigaspacewebconsole.provider;

import com.gigaspaces.cluster.activeelection.SpaceMode;
import com.github.terma.gigaspacewebconsole.core.Count;
import com.github.terma.gigaspacewebconsole.core.CountsRequest;
import com.github.terma.gigaspacewebconsole.core.CountsResponse;
import org.openspaces.admin.Admin;
import org.openspaces.admin.space.Space;
import org.openspaces.admin.space.SpaceInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@SuppressWarnings("WeakerAccess")
public class Counts {

    private static final Logger LOGGER = Logger.getLogger(Counts.class.getName());

    private static Map<String, Integer> instanceToCounts(final SpaceInstance instance) {
        if (instance != null) {
            return instance.getRuntimeDetails().getCountPerClassName();
        } else {
            return new HashMap<>();
        }
    }

    public CountsResponse counts(CountsRequest request) {
        final CountsResponse countsResponse = new CountsResponse();
        countsResponse.counts = new ArrayList<>();

        final Admin admin = AdminLocator.get(request);

        Space space = admin.getSpaces()
                .waitFor(GigaSpaceUrl.parseSpace(request.url), 10, TimeUnit.SECONDS);
        if (space != null) {
            SpaceInstance[] spaceInstances = space.getInstances();
            LOGGER.fine("Admin has " + spaceInstances.length + " space instances");

            final Map<String, Integer> counts = new HashMap<>();
            for (final SpaceInstance spaceInstance : spaceInstances) {
                if (spaceInstance.getMode() != SpaceMode.BACKUP) {

                    // cross space instance types
                    for (final Map.Entry<String, Integer> countItem : instanceToCounts(spaceInstance).entrySet()) {
                        LOGGER.fine("Space instance " + spaceInstance + " has " + countItem + " types");

                        if (request.byPartitions) {
                            counts.put(spaceInstance.getSpaceInstanceName() + " " + countItem.getKey(), countItem.getValue());
                        } else {
                            Integer count = counts.get(countItem.getKey());
                            if (count == null) count = 0;
                            counts.put(countItem.getKey(), count + countItem.getValue());
                        }
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

}
