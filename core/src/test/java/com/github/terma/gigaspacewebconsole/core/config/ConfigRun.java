package com.github.terma.gigaspacewebconsole.core.config;

public class ConfigRun {

    public static void main(String[] args) {
        System.setProperty(Config.CONFIG_PATH_SYSTEM_PROPERTY, Config.LOCAL);

        Config.read();
    }

}
