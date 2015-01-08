package com.github.terma.gigaspacesqlconsole.config;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class Runner {

    public static void main(String[] args) throws Exception {
        System.setProperty(Config.CONFIG_PATH_SYSTEM_PROPERTY, "classpath:/config.json");

        Server server = new Server(8080);
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath("/");
        webAppContext.setWar("target/gigaspace-sql-console-0.0.1-SNAPSHOT.war");
        server.setHandler(webAppContext);
        server.start();
        server.join();
    }

}
