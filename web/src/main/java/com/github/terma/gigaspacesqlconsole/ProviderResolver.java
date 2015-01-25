package com.github.terma.gigaspacesqlconsole;

import com.github.terma.gigaspacesqlconsole.core.Provider;
import com.github.terma.gigaspacesqlconsole.core.config.Config;
import com.github.terma.gigaspacesqlconsole.core.config.ConfigGs;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class ProviderResolver {

    private static final Logger LOGGER = Logger.getLogger(ProviderResolver.class.getSimpleName());

    private static final String PROVIDER_IMPL_CLASS_NAME =
            "com.github.terma.gigaspacesqlconsole.provider.ProviderImpl";

    public static Provider getProvider(final String gs) {
        LOGGER.info("Start getting provider for " + gs + "...");
        final ConfigGs configGs = gsConfigByNameOrFirst(gs);
        LOGGER.info("Find config " + configGs + " for " + gs);
        final URLClassLoader classLoader = createClassLoader(configGs);
        LOGGER.info("Create for " + gs + " classloader " + Arrays.asList(classLoader.getURLs()));
        return getClassInstance(classLoader, PROVIDER_IMPL_CLASS_NAME);
    }

    private static ConfigGs gsConfigByNameOrFirst(final String gsVersion) {
        for (final ConfigGs configGs : Config.read().user.gs) {
            if (configGs.name.equals(gsVersion)) {
                return configGs;
            }
        }
        return Config.read().user.gs.get(0);
    }

    @SuppressWarnings("unchecked")
    private static <T> T getClassInstance(final ClassLoader classLoader, final String className) {
        Thread.currentThread().setContextClassLoader(classLoader);
        Class cls;
        try {
            cls = classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            return (T) cls.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static URLClassLoader createClassLoader(ConfigGs configGs) {
        final List<URL> urls = new ArrayList<>();
        try {
            urls.add(new URL("file:/Users/terma/Projects/gigaspace-sql-console/web/target/gigaspace-sql-console-web-0.0.11-SNAPSHOT/WEB-INF/providers/provider-9.X.jar"));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("// todo", e);
        }

        for (final String lib : configGs.libs) {
            try {
                urls.add(new URL("file:" + lib));
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Incorrect file path to lib: " + lib, e);
            }
        }

        return URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]),
                Thread.currentThread().getContextClassLoader());
    }

}