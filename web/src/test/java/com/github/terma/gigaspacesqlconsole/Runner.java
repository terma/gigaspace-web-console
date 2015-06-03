package com.github.terma.gigaspacesqlconsole;

import com.github.terma.gigaspacesqlconsole.core.config.Config;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class Runner {

    private Server server;

    public void start() {
        // set config
        System.setProperty(Config.CONFIG_PATH_SYSTEM_PROPERTY, "classpath:/config.json");

        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath("/gs-sql-console");
        webAppContext.setWar("target/gigaspace-sql-console-web-0.0.28-SNAPSHOT.war");

        server = new Server(8080);
        server.setHandler(webAppContext);
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
