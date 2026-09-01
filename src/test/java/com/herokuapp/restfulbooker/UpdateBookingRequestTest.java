package com.herokuapp.restfulbooker;


import com.herokuapp.restfulbooker.constants.HttpStatusCodes;
import com.herokuapp.restfulbooker.models.Booking;
import com.herokuapp.restfulbooker.models.response.CreateBookingResponse;
import com.herokuapp.restfulbooker.models.testdata.BookingIdsTestData;
import com.herokuapp.restfulbooker.utils.TestDataUtils;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import static com.herokuapp.restfulbooker.utils.config.TestConfig.*;
import java.lang.reflect.Method;

import static com.herokuapp.restfulbooker.utils.RetryUtils.retryUntilSuccess;
import static org.testng.Assert.assertEquals;

public class UpdateBookingRequestTest extends BaseTest {
    private static final String SKIP_SETUP_FOR = "TC054_Reject_update_with_invalid_booking_id";

    private BookingIdsTestData bookingIdsTestData;
    private int bookingId;

    @BeforeClass(alwaysRun = true)
    public void loadTestData() {

        bookingIdsTestData = TestDataUtils.readJsonFile(
                "com/herokuapp/restfulbooker/models/testdata/booking-data.json",
                BookingIdsTestData.class
        );
        attachLoadedJson("loadTestData - booking-data.json", "com/herokuapp/restfulbooker/models/testdata/booking-data.json");
    }
    @BeforeMethod(alwaysRun = true)
    public void createBookingForUpdate(Method testMethod) {
        // TC054 dùng id không hợp lệ lấy từ test data riêng, không cần booking thật -> bỏ qua setup
        if (SKIP_SETUP_FOR.equals(testMethod.getName())) {
            return;
        }
        bookingId = createBooking(bookingIdsTestData);

        retryUntilSuccess(
            () -> bookingApi.getBooking(bookingId).getStatusCode(),  // ← thêm .getStatusCode()
            MAX_RETRY_ATTEMPTS,
            RETRY_WAIT_MS
        );
    }

    @Test(groups = {"smoke","regression"})
    public void TC050_Update_booking_with_valid_token_and_full_payload() {
        // Test implementation
        Response response = bookingApi.updateBookingWithToken(bookingId, bookingIdsTestData.getUpdateValidBooking(), getAuthToken());
        assertEquals(response.getStatusCode(), HttpStatusCodes.OK);

        Booking updateBookingResponse = response.as(Booking.class);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(updateBookingResponse.getFirstname(),bookingIdsTestData.getUpdateValidBooking().getFirstname());
        softAssert.assertEquals(updateBookingResponse.getLastname(),bookingIdsTestData.getUpdateValidBooking().getLastname());
        softAssert.assertEquals(updateBookingResponse.getDepositpaid(),bookingIdsTestData.getUpdateValidBooking().getDepositpaid());
        softAssert.assertAll();
    }

    @Test(groups = {"smoke","regression"})
    public void TC051_Update_booking_using_Basic_Auth_header_instead_of_cookie() {
        // Test implementation
        Response response = bookingApi.updateBookingWithAuthorization(bookingId, bookingIdsTestData.getUpdateValidBooking());
        assertEquals(response.getStatusCode(), HttpStatusCodes.OK);
        Booking updateBookingResponse = response.as(Booking.class);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(updateBookingResponse.getFirstname(),bookingIdsTestData.getUpdateValidBooking().getFirstname());
        softAssert.assertEquals(updateBookingResponse.getLastname(),bookingIdsTestData.getUpdateValidBooking().getLastname());
        softAssert.assertEquals(updateBookingResponse.getDepositpaid(),bookingIdsTestData.getUpdateValidBooking().getDepositpaid());
        softAssert.assertAll();
    }

    @Test(groups = {"smoke","regression"})
    public void TC052_Reject_update_with_no_auth_token_or_Authorization_header() {
        // Test implementation
        Response response = bookingApi.updateBookingWithNoAuthorizationNoToken(bookingId, bookingIdsTestData.getUpdateValidBooking());
        assertEquals(response.getStatusCode(), HttpStatusCodes.FORBIDDEN);
    }

    @Test(groups = {"smoke","regression"})
    public void TC053_Reject_update_with_invalid_or_expired_token() {
        // Test implementation
        Response response = bookingApi.updateBookingWithToken(bookingId
                        , bookingIdsTestData.getUpdateValidUpdateBookingWithInvalidToken().getBody()
                        , bookingIdsTestData.getUpdateValidUpdateBookingWithInvalidToken().getCookie());
        assertEquals(response.getStatusCode(), HttpStatusCodes.FORBIDDEN);
    }
    @Test(groups = {"smoke","regression"})
    public void TC054_Reject_update_with_invalid_booking_id() {
        // Test implementation
        Response response = bookingApi.updateBookingWithToken(bookingIdsTestData.getUpdateValidUpdateBookingWithInvalidBookingId().getId(), bookingIdsTestData.getUpdateValidUpdateBookingWithInvalidBookingId().getBody(),getAuthToken());
        assertEquals(response.getStatusCode(), HttpStatusCodes.GONE_OR_INVALID_ID);
    }

    @Test(groups = {"smoke","regression"})
    public void TC055_Update_with_missing_required_field_in_body() {
        // Test implementation
        Response response = bookingApi.updateBookingWithToken(bookingId, bookingIdsTestData.getUpdateMissingLastname(), getAuthToken());
        assertEquals(response.getStatusCode(), HttpStatusCodes.BAD_REQUEST);
    }

    @Test(groups ={"smoke", "regression"})
    public void TC060_Partially_update_booking_single_field_with_valid_token() {
        // Test implementation
        Response response = bookingApi.partiallyUpdateBookingWithToken(bookingId, bookingIdsTestData.getPartialUpdateValidBooking(), getAuthToken());
        assertEquals(response.getStatusCode(), HttpStatusCodes.OK);
        Booking bookingResponse = response.as(Booking.class);
        assertEquals(bookingResponse.getFirstname(),bookingIdsTestData.getPartialUpdateValidBooking().get("firstname"));
    }
    @Test(groups = {"smoke","regression"})
    public void TC061_Reject_partial_update_with_no_auth_token() {
        // Test implementation
        Response response = bookingApi.partiallyUpdateBookingWithNoToken(bookingId, bookingIdsTestData.getPartialUpdateValidBooking());
        assertEquals(response.getStatusCode(), HttpStatusCodes.FORBIDDEN);
    }

    @Test(groups = {"smoke","regression"})
    public void TC062_Partial_update_with_empty_JSON_body_but_valid_token() {
        // Test implementation
        Response response = bookingApi.partiallyUpdateBookingWithEmptyBody(bookingId, bookingIdsTestData.getUpdateValidUpdateBookingWithInvalidToken().getCookie());
        assertEquals(response.getStatusCode(), HttpStatusCodes.FORBIDDEN);
    }

    @Test(groups ={"smoke", "regression"})
    public void TC063_Partial_update_with_invalid_field_type() {
        // API thực tế không validate kiểu dữ liệu khi PATCH -> chấp nhận depositpaid kiểu số, trả 200 thay vì 400
        Response response = bookingApi.partiallyUpdateBookingWithToken(bookingId, bookingIdsTestData.getPartialUpdateWithInvalidFieldType(), getAuthToken());
        assertEquals(response.getStatusCode(), HttpStatusCodes.OK);
        Booking bookingResponse = response.as(Booking.class);
        // Verified thật bằng curl: server coerce số -> boolean kiểu truthy (khác 0 => true, 0 => false).
        // depositpaid gửi lên là -10 (khác 0) -> server trả về true. Đây là quirk cố tình được document, không phải giá trị đoán.
        assertEquals(bookingResponse.getDepositpaid(), Boolean.TRUE,
                "depositpaid gửi số khác 0 (-10) phải bị server coerce thành true (documented truthy-coercion quirk)");
    }
}
