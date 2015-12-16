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

package com.github.terma.gigaspacewebconsole.server;

import com.github.terma.gigaspacewebconsole.core.Provider;
import com.github.terma.gigaspacewebconsole.core.config.ConfigDriver;
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

    private static final String GIGSPACE_PROVIDER =
            "com.github.terma.gigaspacewebconsole.provider.GigaSpaceProvider";

    private static final String DATABASE_PROVIDER =
            "com.github.terma.gigaspacewebconsole.provider.DatabaseProvider";

    public static Provider getProvider(final String driver) {
        LOGGER.info("Start getting provider for " + driver + "...");
        final ConfigDriver configDriver = gsConfigByNameOrFirstOrNull(driver);
        final URLClassLoader classLoader = createClassLoader(configDriver);
        LOGGER.info("Create for " + driver + " classloader " + Arrays.asList(classLoader.getURLs()));
        return getClassInstance(classLoader, getProviderClassName(driver));
    }

    private static ConfigDriver gsConfigByNameOrFirstOrNull(final String gs) {
        if (ConfigLocator.CONFIG.user.drivers.isEmpty()) {
            LOGGER.info("No driver configured try to take from classpath");
            return null;
        }

        for (final ConfigDriver configDriver : ConfigLocator.CONFIG.user.drivers) {
            if (configDriver.name.equals(gs)) {
                LOGGER.info("Find config " + configDriver + " for " + configDriver);
                return configDriver;
            }
        }

        final ConfigDriver configDriver = ConfigLocator.CONFIG.user.drivers.get(0);
        LOGGER.info("Can't find config for name: " + gs + ", use first: " + configDriver);
        return configDriver;
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

    private static URLClassLoader createClassLoader(final ConfigDriver configDriver) {
        final List<URL> urls = new ArrayList<>();

        final URL providerLibUrl = ProviderResolver.class.getResource(PROVIDER_LIB_RESOURCE_PATH);
        if (providerLibUrl == null) throw new UnsupportedOperationException(
                "Can't find provider lib in classpath by path: " + PROVIDER_LIB_RESOURCE_PATH);
        urls.add(providerLibUrl);

        if (configDriver != null && configDriver.libs != null) {
            for (final String lib : configDriver.libs) {
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

    private static String getProviderClassName(String driver) {
        if (driver != null && !driver.startsWith("GS") && !driver.startsWith("gs")) {
            return DATABASE_PROVIDER;
        } else {
            return GIGSPACE_PROVIDER;
        }
    }

}