package com.github.terma.gigaspacesqlconsole;

import com.github.terma.gigaspacesqlconsole.core.config.Config;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class Runner {

    public static void main(String[] args) throws Exception {
        // set config
        System.setProperty(Config.CONFIG_PATH_SYSTEM_PROPERTY, "classpath:/config.json");

        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath("/");
        webAppContext.setWar("target/gigaspace-sql-console-0.0.1-SNAPSHOT.war");

        Server server = new Server(8080);
        server.setHandler(webAppContext);
        server.start();
        server.join();
    }

}
