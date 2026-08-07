package com.herokuapp.restfulbooker;

import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class PartialUpdateBookingTest {
    @Test
    public void partialUpdateBookingTest(){
        String  token = AuthUtil.getAuthToken();

        given().headers("Content-Type","application/json")
                .headers("Accept","application/json")
                .headers("Cookie","token=" + token)
                .body("{\n" +
                        "    \"firstname\" : \"James\",\n" +
                        "    \"lastname\" : \"Brown\"\n" +
                        "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/1")
                .then()
                .statusCode(200);
    }
}
