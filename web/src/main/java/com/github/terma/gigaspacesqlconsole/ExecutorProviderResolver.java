package com.github.terma.gigaspacesqlconsole;

import com.github.terma.gigaspacesqlconsole.core.ExecutorProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ExecutorProviderResolver {

    public static ExecutorProvider get() {
        URLClassLoader clsLoader;
        try {
            clsLoader = URLClassLoader.newInstance(
                    new URL[]{new URL("file:/Users/terma/Projects/gigaspace-sql-console/web/target/gigaspace-sql-console-web-0.0.11-SNAPSHOT/WEB-INF/providers/gs-10-0.0.11-SNAPSHOT.jar")},
                    Thread.currentThread().getContextClassLoader());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        Class<ExecutorProvider> cls = null;
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