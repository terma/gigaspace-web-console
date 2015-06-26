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

package com.github.terma.gigaspacewebconsole;

import com.github.terma.gigaspacewebconsole.core.Provider;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class CachedProviderResolver {

    private static final Logger LOGGER = Logger.getLogger(CachedProviderResolver.class.getSimpleName());

    private static final Map<String, Provider> CACHE = new HashMap<>();

    public static synchronized Provider getProvider(final String gs) {
        Provider provider = CACHE.get(gs);
        if (provider == null) {
            LOGGER.info("No provider for " + gs + " creating...");
            provider = ProviderResolver.getProvider(gs);
            CACHE.put(gs, provider);
            LOGGER.info("Provider for " + gs + " created");
            return provider;
        } else {
            LOGGER.info("Return cached provider for " + gs);
            Thread.currentThread().setContextClassLoader(provider.getClass().getClassLoader());
            return provider;
        }
    }

}