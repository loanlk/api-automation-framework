package com.herokuapp.restfulbooker;

import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;

public class HealthCheckTest {
    @Test(groups = {"smoke","regression"})
    public void healthCheckTest(){
        given().
                get("https://restful-booker.herokuapp.com/ping").
                then().statusCode(201);
    }
}
