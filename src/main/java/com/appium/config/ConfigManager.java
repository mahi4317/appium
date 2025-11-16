package com.appium.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigManager {
    private static final Logger logger = Logger.getLogger(ConfigManager.class.getName());
    private static final String DEFAULT_ENV = "android";
    private static Properties properties;

    static {
        load();
    }

    public static void load() {
        String env = System.getProperty("env", DEFAULT_ENV);
        String file = String.format("src/test/resources/config/%s.properties", env);
        properties = new Properties();
        try (InputStream in = new FileInputStream(file)) {
            properties.load(in);
            logger.info("Loaded configuration from " + file);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load configuration: " + file, e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static String get(String key, String def) {
        return properties.getProperty(key, def);
    }
}
