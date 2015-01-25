package com.github.terma.gigaspacesqlconsole;

import com.github.terma.gigaspacesqlconsole.core.Provider;

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