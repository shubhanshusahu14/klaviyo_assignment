package org.example.config;

@FunctionalInterface
public interface ConfigProvider {
    String getProperty(String key);
}
