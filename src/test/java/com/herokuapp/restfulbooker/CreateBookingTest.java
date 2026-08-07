package com.herokuapp.restfulbooker;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class CreateBookingTest {
    @Test
    public void createBookingTest() {
        given().headers("Content-Type", "application/json")
                .body("{\n" +
                        "    \"firstname\" : \"Jim\",\n" +
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
                .post("https://restful-booker.herokuapp.com/booking")
                .then()
                .statusCode(200);
    }

    @Test(groups = {"smoke","regression"})
    public void createBookingWithPOJOTest(){
        //Create body using POJO
        Booking booking = new Booking("Jim", "Brown", 111, true, new BookingDates("2018-01-01", "2019-01-01"), "Breakfast");
        Response response = given().headers("Content-Type","application/json")
                .body(booking)
                .when()
                .post("https://restful-booker.herokuapp.com/booking");
        response.print();
    }
}
