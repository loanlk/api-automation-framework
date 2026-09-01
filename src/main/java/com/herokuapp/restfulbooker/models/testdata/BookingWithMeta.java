package com.herokuapp.restfulbooker.models.testdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.herokuapp.restfulbooker.models.Booking;
import com.herokuapp.restfulbooker.models.response.CreateBookingResponse;

public class BookingWithMeta {
    private Object body;
    @JsonProperty("Accept")           // ← ép khớp đúng key "Accept" trong JSON// "body" là một booking → tái dùng Booking
    private String Accept;
    @JsonProperty("Cookie")
    private String Cookie;
    private int id;
    private String contentType;
    private Booking responseBody;
    private CreateBookingResponse responseBodyWithIDBooking;

    public CreateBookingResponse getResponseBodyWithIDBooking() {
        return responseBodyWithIDBooking;
    }

    public void setResponseBodyWithIDBooking(CreateBookingResponse responseBodyWithIDBooking) {
        this.responseBodyWithIDBooking = responseBodyWithIDBooking;
    }

    public Booking getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(Booking setResponseBody) {
        this.responseBody = setResponseBody;
    }

    public BookingWithMeta() {
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public String getAccept() {
        return Accept;
    }

    public void setAccept(String accept) {
        Accept = accept;
    }

    public String getCookie() {
        return Cookie;
    }

    public void setCookie(String cookie) {
        Cookie = cookie;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
