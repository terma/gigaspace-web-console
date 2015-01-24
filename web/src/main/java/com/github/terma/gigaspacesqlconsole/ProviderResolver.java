package com.github.terma.gigaspacesqlconsole;

import com.github.terma.gigaspacesqlconsole.config.Config;
import com.github.terma.gigaspacesqlconsole.config.ConfigGs;
import com.github.terma.gigaspacesqlconsole.core.CountsProvider;
import com.github.terma.gigaspacesqlconsole.core.ExecutorProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class ProviderResolver {

    private static final String EXECUTOR_PROVIDER_IMPL =
            "com.github.terma.gigaspacesqlconsole.provider.ExecutorProviderImpl";

    private static final String COUNTS_PROVIDER_IMPL =
            "com.github.terma.gigaspacesqlconsole.provider.CountsProviderImpl";

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

    public static ExecutorProvider getExecutor(final String gsVersion) {
        final ConfigGs configGs = gsConfigByNameOrFirst(gsVersion);
        return getClassInstance(createClassLoader(configGs), EXECUTOR_PROVIDER_IMPL);
    }

    private static ConfigGs gsConfigByNameOrFirst(String gsVersion) {
        for (final ConfigGs configGs : Config.read().user.gs) {
            if (configGs.name.equals(gsVersion)) {
                return configGs;
            }
        }
        return Config.read().user.gs.get(0);
    }

    public static CountsProvider getCounts() {
        final ConfigGs configGs = Config.read().user.gs.get(0);
        return getClassInstance(createClassLoader(configGs), COUNTS_PROVIDER_IMPL);
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

        System.out.println("Create classloader for " + urls);
        return URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]),
                Thread.currentThread().getContextClassLoader());
    }

}