package com.herokuapp.restfulbooker;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AuthUtil {
    private static final String AUTH_URL = "https://restful-booker.herokuapp.com/auth";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password123";

    /**
     * Generates a fresh authentication token from the Restful Booker API
     * @return Authentication token string
     */
    public static String getAuthToken() {
        String requestBody = "{\n" +
                "    \"username\" : \"" + USERNAME + "\",\n" +
                "    \"password\" : \"" + PASSWORD + "\"\n" +
                "}";

        Response response = given()
                .headers("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post(AUTH_URL)
                .then()
                .statusCode(200)
                .extract()
                .response();

        return response.jsonPath().getString("token");
    }
}

