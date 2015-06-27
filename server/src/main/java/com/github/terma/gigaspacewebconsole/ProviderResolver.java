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
import com.github.terma.gigaspacewebconsole.core.config.ConfigGs;
import com.github.terma.gigaspacewebconsole.core.config.ConfigLocator;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class ProviderResolver {

    private static final Logger LOGGER = Logger.getLogger(ProviderResolver.class.getSimpleName());

    private static final String PROVIDER_LIB_RESOURCE_PATH = "/provider-9.X.zip";
    private static final String PROVIDER_IMPL_CLASS_NAME =
            "com.github.terma.gigaspacewebconsole.provider.ProviderImpl";

    public static Provider getProvider(final String gs) {
        LOGGER.info("Start getting provider for " + gs + "...");
        final ConfigGs configGs = gsConfigByNameOrFirstOrNull(gs);
        final URLClassLoader classLoader = createClassLoader(configGs);
        LOGGER.info("Create for " + gs + " classloader " + Arrays.asList(classLoader.getURLs()));
        return getClassInstance(classLoader, PROVIDER_IMPL_CLASS_NAME);
    }

    private static ConfigGs gsConfigByNameOrFirstOrNull(final String gs) {
        if (ConfigLocator.CONFIG.user.gs.isEmpty()) {
            LOGGER.info("No gs configured try to take from classpath");
            return null;
        }

        for (final ConfigGs configGs : ConfigLocator.CONFIG.user.gs) {
            if (configGs.name.equals(gs)) {
                LOGGER.info("Find config " + configGs + " for " + configGs);
                return configGs;
            }
        }

        final ConfigGs configGs = ConfigLocator.CONFIG.user.gs.get(0);
        LOGGER.info("Can't find config for name: " + gs + ", use first: " + configGs);
        return configGs;
    }

    @SuppressWarnings("unchecked")
    private static <T> T getClassInstance(final ClassLoader classLoader, final String className) {
        Thread.currentThread().setContextClassLoader(classLoader);

        Class cls;
        try {
            cls = classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Can't load class: " + className, e);
        }

        try {
            return (T) cls.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(
                    "Can't instance by constructor without argument for class: " + cls, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static URLClassLoader createClassLoader(final ConfigGs configGs) {
        final List<URL> urls = new ArrayList<>();

        final URL providerLibUrl = ProviderResolver.class.getResource(PROVIDER_LIB_RESOURCE_PATH);
        if (providerLibUrl == null) throw new UnsupportedOperationException(
                "Can't find provider lib in classpath by path: " + PROVIDER_LIB_RESOURCE_PATH);
        urls.add(providerLibUrl);

        if (configGs != null) {
            for (final String lib : configGs.libs) {
                try {
                    urls.add(new URL("file:" + lib));
                } catch (MalformedURLException e) {
                    throw new IllegalArgumentException("Incorrect file path to lib: " + lib, e);
                }
            }
        }

        return URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]),
                Thread.currentThread().getContextClassLoader());
    }

}