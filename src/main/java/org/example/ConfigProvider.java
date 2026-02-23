package org.example;

@FunctionalInterface
public interface ConfigProvider {
    String getProperty(String key);
}
