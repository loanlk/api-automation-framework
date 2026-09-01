package com.herokuapp.restfulbooker.config;

public class EnvironmentConfig {

    public static String getBaseUrl() {
        return ConfigReader.get("base.url");
    }

    public static String getEnvironment() {
        return ConfigReader.get("environment");
    }
}
