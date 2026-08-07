package com.herokuapp.restfulbooker;

import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class GetBookingTest {
    @Test
    public void GetBookingTest(){
        given()
                .headers("Content-Type","application/json")
                .headers("Accept","application/json")
                .when()
                .get("https://restful-booker.herokuapp.com/booking/1")
                .then()
                .statusCode(200);
    }
}
