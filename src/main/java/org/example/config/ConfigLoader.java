package org.example.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigLoader.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (input != null) {
                properties.load(input);
            } else {
                throw new RuntimeException("application.properties not found");
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties file", e);
        }
    }

    public static String getProperty(String key) {

        String value = properties.getProperty(key);

        if (value == null) {
            return null;
        }

        // Check if value is in format ${ENV_VAR}
        if (value.startsWith("${") && value.endsWith("}")) {

            String envKey = value.substring(2, value.length() - 1);
            String envValue = System.getenv(envKey);

            if (envValue == null) {
                throw new RuntimeException("Environment variable not found: " + envKey);
            }

            return envValue;
        }

        return value;
    }
}