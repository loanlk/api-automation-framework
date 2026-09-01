package com.herokuapp.restfulbooker.api;

import com.herokuapp.restfulbooker.constants.ApiEndpoints;
import io.restassured.response.Response;

public class HealthApi extends BaseApi{
    public Response getPing() {
        return requestSpec()
                .get(ApiEndpoints.HEALTH);
    }

    public HealthApi() {
    }
}
