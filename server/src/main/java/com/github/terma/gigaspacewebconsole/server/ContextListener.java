package com.github.terma.gigaspacewebconsole.server;

import com.github.terma.gigaspacewebconsole.core.config.ConfigLocator;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.logging.Logger;

public class ContextListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(ContextListener.class.getSimpleName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info(ConfigLocator.CONFIG.toString());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

}
