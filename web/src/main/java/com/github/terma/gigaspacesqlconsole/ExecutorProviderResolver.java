package com.github.terma.gigaspacesqlconsole;

import com.github.terma.gigaspacesqlconsole.config.Config;
import com.github.terma.gigaspacesqlconsole.config.ConfigGs;
import com.github.terma.gigaspacesqlconsole.core.ExecutorProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class ExecutorProviderResolver {

    public static ExecutorProvider get() {
        ConfigGs configGs = Config.read().user.gs.get(0);

        List<URL> urls = new ArrayList<>();

        try {
            urls.add(new URL("file:/Users/terma/Projects/gigaspace-sql-console/web/target/gigaspace-sql-console-web-0.0.11-SNAPSHOT/WEB-INF/providers/gs-10-0.0.11-SNAPSHOT.jar"));
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

        URLClassLoader clsLoader = URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]),
                Thread.currentThread().getContextClassLoader());

        Class<ExecutorProvider> cls;
        try {
            cls = (Class<ExecutorProvider>) clsLoader.loadClass("com.github.terma.gigaspacesqlconsole.gs10.ExecutorProviderImpl");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        ExecutorProvider executorProvider = null;
        try {
            executorProvider = cls.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return executorProvider;
    }

}