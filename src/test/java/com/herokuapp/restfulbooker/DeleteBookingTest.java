package com.herokuapp.restfulbooker;

import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class DeleteBookingTest {
    @Test
    public void deleteBookingTest(){
        String token = "token";
        given().headers("Authorization", "Bearer " + token)
                .headers("Content-Type", "application/json")
                .when().delete("https://restful-booker.herokuapp.com/booking/1");
    }
}
