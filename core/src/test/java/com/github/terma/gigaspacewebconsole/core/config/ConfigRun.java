package com.github.terma.gigaspacewebconsole.core.config;

public class ConfigRun {

    public static void main(String[] args) {
        System.setProperty(ConfigFactory.CONFIG_PATH_SYSTEM_PROPERTY, ConfigFactory.LOCAL);

        ConfigFactory.read();
    }

}
