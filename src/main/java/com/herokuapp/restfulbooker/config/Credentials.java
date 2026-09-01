package com.herokuapp.restfulbooker.config;

public class Credentials {
    private Credentials() {}

    // Ưu tiên biến môi trường, fallback về config file
    public static String username() {
        String env = System.getenv("AUTH_USERNAME");
        return env != null ? env : ConfigReader.get("auth.username");
    }

    public static String password() {
        String env = System.getenv("AUTH_PASSWORD");
        return env != null ? env : ConfigReader.get("auth.password");
    }
}
