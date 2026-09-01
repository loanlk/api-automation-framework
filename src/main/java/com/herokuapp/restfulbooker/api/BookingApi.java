package com.herokuapp.restfulbooker.api;

import com.herokuapp.restfulbooker.config.Credentials;
import com.herokuapp.restfulbooker.constants.ApiEndpoints;
import com.herokuapp.restfulbooker.models.BookingFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

public class BookingApi extends BaseApi {

    public Response getBookingIds() {
        return requestSpec()
                .get(ApiEndpoints.BOOKING);
    }

    public Response getBooking(BookingFilter filters) {

        RequestSpecification spec = requestSpec();

        filters.toQueryMap().forEach(spec::queryParam);
        return spec.get(ApiEndpoints.BOOKING);
    }

    public Response getBooking(Map<String, Object> filters) {

        RequestSpecification spec = requestSpec();

        filters.forEach(spec::queryParam);

        return spec
                .log().all()
                .when()
                .get(ApiEndpoints.BOOKING);
    }

    // Path param + Accept header động — nhận Object để chấp nhận cả id không hợp lệ (vd "abc") cho negative test
    public Response getBooking(Object bookingId, String acceptHeader) {
        return requestSpec(acceptHeader)
                .pathParam("id", bookingId)
                .get(ApiEndpoints.BOOKING + "/{id}");
    }

    public Response getBooking(Object bookingId) {
        return requestSpec()
                .pathParam("id", bookingId)
                .get(ApiEndpoints.BOOKING + "/{id}");
    }

    public Response createBooking(Object bookingBody) {
        return requestSpec()
                .body(bookingBody)
                .post(ApiEndpoints.BOOKING);
    }

    public Response createBooking(Object bookingBody, String acceptHeader, String contentType) {
        return requestSpec().accept(acceptHeader)
                .contentType(contentType)
                .body(bookingBody)
                .post(ApiEndpoints.BOOKING);
    }

    // "rawFormBody" đã là chuỗi query-string hoàn chỉnh (vd "firstname=Jim&lastname=Brown&...")
    // -> gửi thẳng làm body, KHÔNG dùng .formParam() (formParam sẽ set field tên là "body"/"contentType" thay vì field thật)
    public Response createBookingFormUrlEncoded(String rawFormBody, String contentType) {
        return requestSpec()
                .contentType(contentType)
                .body(rawFormBody)
                .post(ApiEndpoints.BOOKING);
    }


    public Response createBooking() {
        return requestSpec()
                .post(ApiEndpoints.BOOKING);
    }

    public Response updateBookingWithToken(Object bookingId, Object bookingBody, String token) {
        return requestSpec()
                .cookie("token", token)
                .pathParam("id", bookingId)
                .body(bookingBody)
                .put(ApiEndpoints.BOOKING + "/{id}");
    }

    public Response updateBookingWithAuthorization(Object bookingId, Object bookingBody) {
        return requestSpec()
                .auth().preemptive().basic(Credentials.username(), Credentials.password())
                .pathParam("id", bookingId)
                .body(bookingBody)
                .put(ApiEndpoints.BOOKING + "/{id}");
    }

    public Response updateBookingWithNoAuthorizationNoToken(Object bookingId, Object bookingBody) {
        return requestSpec()
                .pathParam("id", bookingId)
                .body(bookingBody)
                .put(ApiEndpoints.BOOKING + "/{id}");
    }

    public Response partiallyUpdateBookingWithToken(Object bookingId, Object bookingBody, String token) {
        return requestSpec()
                .cookie("token", token)
                .pathParam("id", bookingId)
                .body(bookingBody)
                .patch(ApiEndpoints.BOOKING + "/{id}");
    }

    public Response partiallyUpdateBookingWithNoToken(Object bookingId, Object bookingBody) {
        return requestSpec()
                .pathParam("id", bookingId).contentType("application/json")
                .body(bookingBody)
                .patch(ApiEndpoints.BOOKING + "/{id}");
    }

    public Response partiallyUpdateBookingWithEmptyBody(Object bookingId,String token) {
        return requestSpec()
                .cookie("token",token)
                .pathParam("id", bookingId)
                .patch(ApiEndpoints.BOOKING + "/{id}");
    }
    public Response deleteBookingWithToken(Object bookingId, String token) {
        return requestSpec()
                .cookie("token",token)
                .pathParam("id", bookingId)
                .delete(ApiEndpoints.BOOKING + "/{id}");
    }
    public Response deleteBookingWithNoTokenAuthorization(Object bookingId) {
        return requestSpec()
                .pathParam("id", bookingId)
                .delete(ApiEndpoints.BOOKING + "/{id}");
    }
}
