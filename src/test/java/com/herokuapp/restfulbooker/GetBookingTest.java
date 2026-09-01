package com.herokuapp.restfulbooker;

import com.herokuapp.restfulbooker.constants.HttpStatusCodes;
import com.herokuapp.restfulbooker.models.Booking;
import com.herokuapp.restfulbooker.models.testdata.BookingIdsTestData;
import com.herokuapp.restfulbooker.models.testdata.BookingWithMeta;
import com.herokuapp.restfulbooker.models.testdata.IdCase;
import com.herokuapp.restfulbooker.utils.TestDataUtils;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class GetBookingTest extends BaseTest {
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

    // Không dùng id cố định (vd 1) vì server dùng chung có thể không còn booking đó -> tự tạo booking mới mỗi lần
    @BeforeMethod(alwaysRun = true)
    public void createBookingForGet() {
        bookingId = (int) bookingApi.createBooking(bookingIdsTestData.getCreateValidBooking())
                .then().extract().path("bookingid");

        attachCurlEvidence("createBookingForGet - create booking");
        attachReturnValue("createBookingForGet - bookingId", bookingId);
    }

    @Test(groups = {"smoke", "regression"})
    public void TC030_Retrieve_an_existing_booking_by_valid_ID(){
        Booking responseBooking = bookingIdsTestData.getCreateValidBooking();

        BookingWithMeta filters = bookingIdsTestData.getValidID();

        Response response = bookingApi.getBooking(bookingId, filters.getAccept());
        assertEquals(response.getStatusCode(), HttpStatusCodes.OK);

        boolean matchesSchema = JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/getBookingbyIdSchema.json"))
                .matches(response.getBody().asString());
        assertTrue(matchesSchema,"response body does not match getBookingbyIdSchema.json");

        Booking bookingResponse = response.as(Booking.class);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(bookingResponse.getLastname(),responseBooking.getLastname(),"lastname mismatch");
        softAssert.assertEquals(bookingResponse.getFirstname(),responseBooking.getFirstname(),"firstname mismatch");
        softAssert.assertEquals(bookingResponse.getTotalprice(), responseBooking.getTotalprice(), "totalprice mismatch");
        softAssert.assertAll();
    }

    @Test(groups = {"smoke", "regression"})
    public void TC031_Retrieve_booking_as_XML_via_Accept_header(){
        Booking responseBooking = bookingIdsTestData.getCreateValidBooking();

        BookingWithMeta filters = bookingIdsTestData.getValidIDinXML();

        Response response = bookingApi.getBooking(bookingId, filters.getAccept());
        assertEquals(response.getStatusCode(), HttpStatusCodes.OK);

        // Response là XML thật (Accept: application/xml) — KHÔNG dùng JsonSchemaValidator/.as(Booking.class),
        // 2 API đó chỉ hiểu JSON và sẽ throw JsonParseException khi gặp '<'.
        // Dùng XmlPath (có sẵn trong rest-assured, không cần thêm dependency) để extract field, giống JsonPath cho JSON.
        XmlPath xmlPath = response.xmlPath();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(xmlPath.getString("booking.lastname"), responseBooking.getLastname(), "lastname mismatch");
        softAssert.assertEquals(xmlPath.getString("booking.firstname"), responseBooking.getFirstname(), "firstname mismatch");
        softAssert.assertEquals(xmlPath.getInt("booking.totalprice"), responseBooking.getTotalprice().intValue(), "totalprice mismatch");
        softAssert.assertEquals(xmlPath.getBoolean("booking.depositpaid"), responseBooking.getDepositpaid().booleanValue(), "depositpaid mismatch");
        softAssert.assertEquals(xmlPath.getString("booking.bookingdates.checkin"), responseBooking.getBookingdates().getCheckin(), "checkin mismatch");
        softAssert.assertEquals(xmlPath.getString("booking.bookingdates.checkout"), responseBooking.getBookingdates().getCheckout(), "checkout mismatch");
        softAssert.assertAll();
    }

    @Test(groups = {"smoke", "regression"})
    public void TC032_Request_a_booking_ID_that_does_not_exist(){

        IdCase filters = bookingIdsTestData.getNotExist();

        Response response = bookingApi.getBooking(filters.getId());
        response.then().log().all().statusCode(HttpStatusCodes.NOT_FOUND);
    }

    @Test(groups = {"smoke", "regression"})
    public void TC033_Request_booking_with_non_numeric_ID(){

        IdCase filters = bookingIdsTestData.getNonNumericID();

        Response response = bookingApi.getBooking(filters.getId());
        response.then().log().all().statusCode(HttpStatusCodes.NOT_FOUND);
    }

    @Test(groups = {"smoke", "regression"})
    public void TC034_RRequest_booking_with_unsupported_Accept_header(){

        IdCase filters = bookingIdsTestData.getValidIDinAppPdf();

        Response response = bookingApi.getBooking(bookingId, filters.getAccept());
        response.then().log().all().statusCode(HttpStatusCodes.TEAPOT_UNSUPPORTED_ACCEPT);
    }
}
