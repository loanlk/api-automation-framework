package com.herokuapp.restfulbooker.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final Properties properties = new Properties();

    static {
        // -Denv=qa|staging chọn file properties tương ứng, mặc định "qa" nếu không truyền.
        // Xem pom.xml: <properties><env>qa</env></properties> + surefire <systemPropertyVariables>
        // để -Denv=... trên dòng lệnh mvn thực sự được forward vào JVM chạy test.
        String env = System.getProperty("env", "qa");
        String resourcePath = "config/" + env + ".properties";

        try (InputStream input =
                     ConfigReader.class
                             .getClassLoader()
                             .getResourceAsStream(resourcePath)) {

            if (input == null) {
                throw new RuntimeException(
                        resourcePath + " was not found");
            }

            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load configuration: " + resourcePath, e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}

