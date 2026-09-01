package com.herokuapp.restfulbooker;

import com.herokuapp.restfulbooker.constants.HttpStatusCodes;
import com.herokuapp.restfulbooker.models.testdata.BookingIdsTestData;
import com.herokuapp.restfulbooker.utils.TestDataUtils;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.herokuapp.restfulbooker.utils.RetryUtils.retryUntilSuccess;
import static com.herokuapp.restfulbooker.utils.config.TestConfig.MAX_RETRY_ATTEMPTS;
import static com.herokuapp.restfulbooker.utils.config.TestConfig.RETRY_WAIT_MS;

public class DeleteBookingRequestTest extends BaseTest {

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
    public void createBookingForDelete() {

        bookingId = createBooking(bookingIdsTestData);
        retryUntilSuccess(
                () -> bookingApi.getBooking(bookingId).getStatusCode(),  // ← thêm .getStatusCode()
                MAX_RETRY_ATTEMPTS,
                RETRY_WAIT_MS
        );
    }

    @Test(groups = {"smoke","regression"})
    public void TC070_Delete_booking_with_valid_token() {
        // Test implementation
        Response response = bookingApi.deleteBookingWithToken(bookingId, getAuthToken());
        Assert.assertEquals(response.getStatusCode(), HttpStatusCodes.CREATED);
    }

    @Test(groups = {"smoke","regression"})
    public void TC071_Delete_booking_with_no_auth_token() {
        // Test implementation
        Response response = bookingApi.deleteBookingWithNoTokenAuthorization(bookingId);
        Assert.assertEquals(response.getStatusCode(), HttpStatusCodes.FORBIDDEN);
    }

    @Test(groups = {"smoke","regression"})
    public void TC072_Delete_a_booking_ID_that_does_not_exist() {
        // Test implementation
        Response response = bookingApi.deleteBookingWithToken(bookingIdsTestData.getDeleteWithNotExistingBookingId().getId(), getAuthToken());
        Assert.assertEquals(response.getStatusCode(), HttpStatusCodes.GONE_OR_INVALID_ID);
    }

    @Test(groups = {"smoke","regression"})
    public void TC073_Delete_the_same_booking_a_second_time_idempotency_check_() {
        String token = getAuthToken();
        // Test implementation
        Response response = bookingApi.deleteBookingWithToken(bookingId, token);
        Assert.assertEquals(response.getStatusCode(), HttpStatusCodes.CREATED);

        Response response2 = bookingApi.deleteBookingWithToken(bookingId,token);
        Assert.assertEquals(response2.getStatusCode(), HttpStatusCodes.GONE_OR_INVALID_ID);
    }

    @Test(groups = {"smoke","regression"})
    public void TC074_Verify_deleted_booking_is_no_longer_retrievable_chained_test(){
        // Test implementation
        Response response = bookingApi.deleteBookingWithToken(bookingId, getAuthToken());
        Assert.assertEquals(response.getStatusCode(), HttpStatusCodes.CREATED);

        Response response2 = bookingApi.getBooking(bookingId);
        Assert.assertEquals(response2.getStatusCode(), HttpStatusCodes.NOT_FOUND);
    }
}
