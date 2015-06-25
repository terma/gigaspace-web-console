package com.github.terma.gigaspacesqlconsole.core.config;

public class ConfigRun {

    public static void main(String[] args) {
        System.setProperty(Config.CONFIG_PATH_SYSTEM_PROPERTY, Config.LOCAL);

        Config.read();
    }

}
