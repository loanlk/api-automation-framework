package com.herokuapp.restfulbooker;

import com.herokuapp.restfulbooker.constants.HttpStatusCodes;
import com.herokuapp.restfulbooker.models.Booking;
import com.herokuapp.restfulbooker.models.response.CreateBookingResponse;
import com.herokuapp.restfulbooker.models.testdata.BookingIdsTestData;
import com.herokuapp.restfulbooker.models.testdata.BookingWithMeta;
import com.herokuapp.restfulbooker.utils.TestDataUtils;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.File;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class CreateBookingRequestTest extends BaseTest {
    private BookingIdsTestData bookingIdsTestData;

    @BeforeClass(alwaysRun = true)
    public void loadTestData() {

        bookingIdsTestData = TestDataUtils.readJsonFile(
                "com/herokuapp/restfulbooker/models/testdata/booking-data.json",
                BookingIdsTestData.class
        );
        attachLoadedJson("loadTestData - booking-data.json", "com/herokuapp/restfulbooker/models/testdata/booking-data.json");
    }
    @Test(groups = {"smoke", "regression"})
    public void TC040_Create_booking_with_valid_full_payload() {
        Booking requestBooking = bookingIdsTestData.getCreateValidBooking();

        Response response = bookingApi.createBooking(bookingIdsTestData.getCreateValidBooking());
        assertEquals(response.getStatusCode(), HttpStatusCodes.OK);

        boolean matchesSchema = JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/createBookingSchema.json"))
                .matches(response.getBody().asString());
        assertTrue(matchesSchema,"response body does not match createBookingSchema.json");

        CreateBookingResponse createBookingResponse = response.as(CreateBookingResponse.class);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(createBookingResponse.getBookingid()>0, "bookingid should be positive");
        softAssert.assertEquals(createBookingResponse.getBooking().getLastname(),requestBooking.getLastname(),"lastname mismatch");
        softAssert.assertEquals(createBookingResponse.getBooking().getFirstname(),requestBooking.getFirstname(),"firstname mismatch");
        softAssert.assertEquals(createBookingResponse.getBooking().getTotalprice(), requestBooking.getTotalprice(), "totalprice mismatch");
        softAssert.assertAll();
    }

    @Test(groups = {"smoke", "regression"})
    public void TC041_Create_booking_missing_required_firstname() {
        Response response = bookingApi.createBooking(bookingIdsTestData.getCreateMissingFirstnameRequired());
        assertEquals(response.getStatusCode(), HttpStatusCodes.VALIDATION_ERROR);
    }

    @Test(groups = {"smoke", "regression"})
    public void TC042_Create_booking_with_wrong_data_type_for_totalprice() {
        Response response = bookingApi.createBooking(bookingIdsTestData.getCreateWrongPriceDataType());
        assertEquals(response.getStatusCode(), HttpStatusCodes.VALIDATION_ERROR);
    }

    @Test(groups = {"smoke", "regression"})
    public void TC043_Create_booking_where_checkout_date_precedes_checkin_date() {
        Booking requestBooking = bookingIdsTestData.getCreateCheckoutDateprecedesCheckinDate();

        Response response = bookingApi.createBooking(bookingIdsTestData.getCreateCheckoutDateprecedesCheckinDate());
        assertEquals(response.getStatusCode(), HttpStatusCodes.OK);

        boolean matchesSchema = JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/createBookingSchema.json"))
                .matches(response.getBody().asString());
        assertTrue(matchesSchema,"response body does not match createBookingSchema.json");

        CreateBookingResponse createBookingResponse = response.as(CreateBookingResponse.class);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(createBookingResponse.getBookingid()>0, "bookingid should be positive");
        softAssert.assertEquals(createBookingResponse.getBooking().getLastname(),requestBooking.getLastname(),"lastname mismatch");
        softAssert.assertEquals(createBookingResponse.getBooking().getFirstname(),requestBooking.getFirstname(),"firstname mismatch");
        softAssert.assertEquals(createBookingResponse.getBooking().getTotalprice(), requestBooking.getTotalprice(), "totalprice mismatch");
        softAssert.assertAll();
    }

    @Test(groups = {"smoke", "regression"})
    public void TC044_Create_booking_with_totalprice_0_and_negative_value() {
        Booking requestBooking = bookingIdsTestData.getCreateNegativeTotalPrice();

        Response response = bookingApi.createBooking(bookingIdsTestData.getCreateNegativeTotalPrice());
        assertEquals(response.getStatusCode(), HttpStatusCodes.OK);
        boolean matchesSchema = JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/createBookingSchema.json"))
                .matches(response.getBody().asString());
        assertTrue(matchesSchema,"response body does not match createBookingSchema.json");

        CreateBookingResponse createBookingResponse = response.as(CreateBookingResponse.class);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(createBookingResponse.getBookingid()>0, "bookingid should be positive");
        softAssert.assertEquals(createBookingResponse.getBooking().getLastname(),requestBooking.getLastname(),"lastname mismatch");
        softAssert.assertEquals(createBookingResponse.getBooking().getFirstname(),requestBooking.getFirstname(),"firstname mismatch");
        softAssert.assertEquals(createBookingResponse.getBooking().getTotalprice(), requestBooking.getTotalprice(), "totalprice mismatch");
        softAssert.assertAll();
    }

    @Test(groups = {"smoke", "regression"})
    public void TC045_Create_booking_using_XML_content_type() {
        Booking requestBooking = bookingIdsTestData.getCreateValidBookingInXML().getResponseBody();
        Response response = bookingApi.createBooking(bookingIdsTestData.getCreateValidBookingInXML().getBody(),bookingIdsTestData.getCreateValidBookingInXML().getAccept(),bookingIdsTestData.getCreateValidBookingInXML().getContentType());
        assertEquals(response.getStatusCode(), HttpStatusCodes.OK);

        XmlPath xmlPath = response.xmlPath();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(xmlPath.getInt("created-booking.bookingid") >0 , "bookingid should be positive");
        softAssert.assertEquals(xmlPath.getString("created-booking.booking.lastname"),requestBooking.getLastname(),"lastname mismatch");
        softAssert.assertEquals(xmlPath.getString("created-booking.booking.firstname"),requestBooking.getFirstname(),"firstname mismatch");
        softAssert.assertEquals(xmlPath.getInt("created-booking.booking.totalprice"), requestBooking.getTotalprice().intValue(), "totalprice mismatch");
        softAssert.assertAll();
    }

    @Test(groups = {"smoke", "regression"})
    public void TC046_Create_booking_using_form_urlencoded_content_type() {
        Booking requestBooking = bookingIdsTestData.getCreateValidBookingInFormUrlEncoded().getResponseBody();

        BookingWithMeta formData = bookingIdsTestData.getCreateValidBookingInFormUrlEncoded();
        Response response = bookingApi.createBookingFormUrlEncoded(
                formData.getBody().toString(), formData.getContentType());
        assertEquals(response.getStatusCode(), HttpStatusCodes.OK);

        boolean matchesSchema = JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/createBookingSchema.json"))
                .matches(response.getBody().asString());
        assertTrue(matchesSchema,"response body does not match createBookingSchema.json");

        CreateBookingResponse createBookingResponse = response.as(CreateBookingResponse.class);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(createBookingResponse.getBookingid()>0, "bookingid should be positive");
        softAssert.assertEquals(createBookingResponse.getBooking().getLastname(),requestBooking.getLastname(),"lastname mismatch");
        softAssert.assertEquals(createBookingResponse.getBooking().getFirstname(),requestBooking.getFirstname(),"firstname mismatch");
        softAssert.assertEquals(createBookingResponse.getBooking().getTotalprice(), requestBooking.getTotalprice(), "totalprice mismatch");
        softAssert.assertAll();
    }

    @Test(groups = {"smoke", "regression"})
    public void TC047_Create_booking_with_empty_request_body() {
        Response response = bookingApi.createBooking();
        assertEquals(response.getStatusCode(), HttpStatusCodes.VALIDATION_ERROR);

    }
    @Test(groups = {"smoke", "regression"})
    public void TC048_Create_booking_using_SQL_injection() {
        Booking requestBooking = bookingIdsTestData.getCreateSQLinjectFirstname();

        Response response = bookingApi.createBooking(bookingIdsTestData.getCreateSQLinjectFirstname());
        assertEquals(response.getStatusCode(), HttpStatusCodes.OK);

        boolean matchesSchema = JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/createBookingSchema.json"))
                .matches(response.getBody().asString());
        assertTrue(matchesSchema,"response body does not match createBookingSchema.json");

        CreateBookingResponse createBookingResponse = response.as(CreateBookingResponse.class);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(createBookingResponse.getBookingid()>0, "bookingid should be positive");
        softAssert.assertEquals(createBookingResponse.getBooking().getLastname(),requestBooking.getLastname(),"lastname mismatch");
        softAssert.assertEquals(createBookingResponse.getBooking().getFirstname(),requestBooking.getFirstname(),"firstname mismatch");
        softAssert.assertEquals(createBookingResponse.getBooking().getTotalprice(), requestBooking.getTotalprice(), "totalprice mismatch");
        softAssert.assertAll();

    }

    @Test(groups = {"smoke", "regression"})
    public void TC049_Create_booking_with_very_long_string_values() {
        Response response = bookingApi.createBooking(bookingIdsTestData.getCreateBookingWithVeryLongStringValues());
        assertEquals(response.getStatusCode(), HttpStatusCodes.VALIDATION_ERROR);
    }
}
