package com.herokuapp.restfulbooker;

import com.herokuapp.restfulbooker.constants.HttpStatusCodes;
import com.herokuapp.restfulbooker.models.BookingFilter;
import com.herokuapp.restfulbooker.models.testdata.BookingIdsTestData;
import com.herokuapp.restfulbooker.utils.TestDataUtils;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class GetBookingIdsTest extends BaseTest {

    private BookingIdsTestData bookingIdsTestData;

    @BeforeClass(alwaysRun = true)
    public void loadTestData() {

        bookingIdsTestData = TestDataUtils.readJsonFile(
                "com/herokuapp/restfulbooker/models/testdata/bookingIds-data.json",
                BookingIdsTestData.class
        );
        attachLoadedJson("loadTestData - bookingIds-data.json", "com/herokuapp/restfulbooker/models/testdata/bookingIds-data.json");
    }

    @Test(groups = {"smoke", "regression"})
    public void TC_020_Retrieve_all_booking_IDs_with_no_filters() {
        Response response = bookingApi.getBookingIds();
        assertEquals(response.getStatusCode(), HttpStatusCodes.OK);
        boolean matchesSchema = JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/getBookingIdsSchema.json"))
                        .matches(response.getBody().asString());
        assertTrue(matchesSchema);
    }

    @Test(groups = {"smoke", "regression"})
    public void TC_021_Filter_bookings_by_firstname_and_lastname() {
        BookingFilter filters =
                bookingIdsTestData.getFilterByFirstnameLastname();

        Response response = bookingApi.getBooking(filters);
        assertEquals(response.getStatusCode(), HttpStatusCodes.OK);

        boolean matchesSchema = JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/getBookingIdsSchema.json"))
                .matches(response.getBody().asString());
        assertTrue(matchesSchema);
    }

    @Test(groups ={"smoke","regression"})
    public void TC_022_Filter_bookings_by_checkin_checkout_date_range(){
        BookingFilter filters =
                bookingIdsTestData.getFilterCheckinCheckout();

        Response response = bookingApi.getBooking(filters);

        assertEquals(response.getStatusCode(), HttpStatusCodes.OK);

        boolean matchesSchema = JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/getBookingIdsSchema.json"))
                .matches(response.getBody().asString());
        assertTrue(matchesSchema);
    }

    @Test
    public void TC_023_Filter_by_a_name_that_matches_no_booking_returns_empty_set(){
        BookingFilter filters =
                bookingIdsTestData.getFilterNameNoBookingFound();

        Response response = bookingApi.getBooking(filters);

        assertEquals(response.getStatusCode(), HttpStatusCodes.OK);

        boolean matchesSchema = JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/getBookingIdsSchema.json"))
                .matches(response.getBody().asString());
        assertTrue(matchesSchema);
    }

    @Test(groups ={"smoke","regression"})
    public void TC_024_Malformed_date_value_in_checkin_filter(){
        Map<String, Object> filters = bookingIdsTestData.getFilterMalformedDate();

        Response response = bookingApi.getBooking(filters);
        assertEquals(response.getStatusCode(), HttpStatusCodes.VALIDATION_ERROR);
    }

    @Test(groups ={"smoke","regression"})
    public void TC_025_XSS_payload_in_firstname_filter_is_not_reflected_unescaped(){
        Map<String, Object> filters = bookingIdsTestData.getFilterXSSPayloadInfirstname();

        Response response = bookingApi.getBooking(filters);
        assertEquals(response.getStatusCode(), HttpStatusCodes.OK);

        boolean matchesSchema = JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/getBookingIdsSchema.json"))
                .matches(response.getBody().asString());
        assertTrue(matchesSchema);
    }
}
