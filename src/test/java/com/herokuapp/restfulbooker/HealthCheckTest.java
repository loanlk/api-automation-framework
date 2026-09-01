package com.herokuapp.restfulbooker;

import com.herokuapp.restfulbooker.constants.HttpStatusCodes;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HealthCheckTest extends BaseTest {

    @Test(groups = {"smoke","regression"})
    public void healthCheckTest(){
        Response response = healthApi.getPing();
        Assert.assertEquals(response.getStatusCode(), HttpStatusCodes.CREATED);
    }
}
