package com.github.terma.gigaspacesqlconsole.config;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class Config {

    public static final String CONFIG_PATH_SYSTEM_PROPERTY = "gigaspaceSqlConsoleConfig";

    private static final String CLASSPATH_PREFIX = "classpath:";
    private static final String FILE_PREFIX = "file:";

    public static Config read() {
        final String configPath = System.getProperty(CONFIG_PATH_SYSTEM_PROPERTY);
        if (configPath == null)
            throw new IllegalArgumentException("Add " + CONFIG_PATH_SYSTEM_PROPERTY + " to system properties!");

        if (configPath.startsWith(CLASSPATH_PREFIX)) {
            final InputStream configStream = Config.class.getResourceAsStream(configPath.substring(CLASSPATH_PREFIX.length()));
            if (configStream == null) throw new IllegalArgumentException("Can't load config from: " + configPath);

            final InputStreamReader reader = new InputStreamReader(configStream);
            return new Gson().fromJson(reader, Config.class);
        } else if (configPath.startsWith(FILE_PREFIX)) {
            try {
                return new Gson().fromJson(new FileReader(configPath.substring(FILE_PREFIX.length())), Config.class);
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException("Can't load config from: " + configPath);
            }
        }

        throw new IllegalArgumentException("Unknown config path: " + configPath);
    }

    public List<ConfigLink> links;
    public List<String> converters;
    public List<ConfigGigaSpace> gigaspaces;

}
