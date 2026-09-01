package com.herokuapp.restfulbooker.api;

import com.herokuapp.restfulbooker.constants.ApiEndpoints;
import com.herokuapp.restfulbooker.models.request.AuthRequest;
import io.restassured.response.Response;

public class AuthApi extends BaseApi {

    public Response login(String username, String password) {
        AuthRequest authRequest = new AuthRequest(username, password);
        return requestSpec()
                .body(authRequest).log().all()
                .post(ApiEndpoints.AUTH);
    }

    public Response submitAuthWithoutBody() {
        return requestSpec()
                .post(ApiEndpoints.AUTH);
    }
}
