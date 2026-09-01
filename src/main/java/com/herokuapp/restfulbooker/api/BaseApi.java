package com.herokuapp.restfulbooker.api;

import com.herokuapp.restfulbooker.config.EnvironmentConfig;
import com.herokuapp.restfulbooker.utils.CurlLoggingFilter;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;

public class BaseApi {
    protected RequestSpecification requestSpec(){
        return given()
                .config(config().encoderConfig(
                        encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                .baseUri(EnvironmentConfig.getBaseUrl())
                .contentType(ContentType.JSON)
                .filter(new CurlLoggingFilter())     // ← thêm dòng này
                .filter(new AllureRestAssured());}

    protected RequestSpecification requestSpec(String acceptHeader) {
        return requestSpec().contentType(acceptHeader).accept(acceptHeader);
    }
}
