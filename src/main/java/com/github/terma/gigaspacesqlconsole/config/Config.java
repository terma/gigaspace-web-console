package com.github.terma.gigaspacesqlconsole.config;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

// todo think about cache configuration as no reason to reload each time
public class Config {

    public static final String CONFIG_PATH_SYSTEM_PROPERTY = "gigaspaceSqlConsoleConfig";

    private static final String CLASSPATH_PREFIX = "classpath:";
    private static final String FILE_PREFIX = "file:";

    private static final String INTERNAL_CONFIG_PATH = "/internalConfig.json";

    public static Config read() {
        final Config config = new Config();
        config.internal = readInternal();
        config.user = readUser();
        return config;
    }

    // package only for tests
    static InternalConfig readInternal() {
        final InputStream versionStream = Config.class.getResourceAsStream(INTERNAL_CONFIG_PATH);
        final InputStreamReader reader = new InputStreamReader(versionStream);
        return new Gson().fromJson(reader, InternalConfig.class);
    }

    // package only for tests
    static UserConfig readUser() {
        final String configPath = System.getProperty(CONFIG_PATH_SYSTEM_PROPERTY);
        if (configPath == null)
            throw new IllegalArgumentException("Add " + CONFIG_PATH_SYSTEM_PROPERTY + " to system properties!");

        if (configPath.startsWith(CLASSPATH_PREFIX)) {
            final InputStream configStream = UserConfig.class.getResourceAsStream(configPath.substring(CLASSPATH_PREFIX.length()));
            if (configStream == null) throw new IllegalArgumentException("Can't load config from: " + configPath);

            final InputStreamReader reader = new InputStreamReader(configStream);
            return new Gson().fromJson(reader, UserConfig.class);
        } else if (configPath.startsWith(FILE_PREFIX)) {
            try {
                return new Gson().fromJson(new FileReader(configPath.substring(FILE_PREFIX.length())), UserConfig.class);
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException("Can't load config from: " + configPath);
            }
        }

        throw new IllegalArgumentException("Unknown config path: " + configPath);
    }

    public UserConfig user;
    public InternalConfig internal;

}
