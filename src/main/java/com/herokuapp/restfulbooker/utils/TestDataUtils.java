package com.herokuapp.restfulbooker.utils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class TestDataUtils {

    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private TestDataUtils() {
    }

    public static <T> T readJsonFile(
            String filePath,
            Class<T> clazz) {

        try (InputStream inputStream =
                     TestDataUtils.class
                             .getClassLoader()
                             .getResourceAsStream(filePath)) {

            if (inputStream == null) {
                throw new RuntimeException(
                        "Test data file not found: " + filePath
                );
            }

            return OBJECT_MAPPER.readValue(
                    inputStream,
                    clazz
            );

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to read test data: " + filePath,
                    e
            );
        }
    }
}