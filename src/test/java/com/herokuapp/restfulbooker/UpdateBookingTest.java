package com.herokuapp.restfulbooker;

import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class UpdateBookingTest {
    @Test
    public void UpdateBookingTest(){
        String token = AuthUtil.getAuthToken();

        given()
                .headers("Content-Type","application/json")
                .headers("Accept","application/json")
                .headers("Cookie","token=" + token)
                .body("{" +
                        "    \"firstname\" : \"James\",\n" +
                        "    \"lastname\" : \"Brown\",\n" +
                        "    \"totalprice\" : 111,\n" +
                        "    \"depositpaid\" : true,\n" +
                        "    \"bookingdates\" : {\n" +
                        "        \"checkin\" : \"2018-01-01\",\n" +
                        "        \"checkout\" : \"2019-01-01\"\n" +
                        "    },\n" +
                        "    \"additionalneeds\" : \"Breakfast\"\n" +
                        "}")
                .when()
                .put("https://restful-booker.herokuapp.com/booking/1")
                .then()
                .statusCode(200);
    }
}
