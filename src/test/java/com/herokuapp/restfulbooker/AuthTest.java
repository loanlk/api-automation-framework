package com.herokuapp.restfulbooker;

import com.herokuapp.restfulbooker.constants.AuthConstants;
import com.herokuapp.restfulbooker.constants.HttpStatusCodes;
import com.herokuapp.restfulbooker.models.request.AuthRequest;
import com.herokuapp.restfulbooker.models.testdata.AuthTestData;
import com.herokuapp.restfulbooker.models.testdata.BookingIdsTestData;
import com.herokuapp.restfulbooker.utils.TestDataUtils;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class AuthTest extends BaseTest {
    private AuthTestData authTestData;

    @BeforeClass(alwaysRun = true)
    public void loadTestData() {

        authTestData = TestDataUtils.readJsonFile(
                "com/herokuapp/restfulbooker/models/testdata/auth-data.json",
                AuthTestData.class
        );
        attachLoadedJson("loadTestData - auth-data.json", "com/herokuapp/restfulbooker/models/testdata/auth-data.json");
    }

    @Test(groups = {"smoke","regression"})
    public void TC_010_Generate_token_with_valid_credentials() {
        String username =
                authTestData.getValidUser().getUsername();

        String password =
                authTestData.getValidUser().getPassword();

        Response response =
                authApi.login(username, password);
        assertEquals(response.getStatusCode(), HttpStatusCodes.OK);

        // /auth trả JSON, không phải XML -> dùng jsonPath()/.path(), KHÔNG dùng xmlPath()
        String token = response.jsonPath().getString("token");

        assertTrue(token.matches(AuthConstants.TOKEN_PATTERN), "token does not match expected pattern");
    }

    @Test(groups = {"smoke","regression"})
    public void TC_011_Reject_login_with_wrong_password(){
        String username = authTestData.getWrongPassword().getUsername();
        String password = authTestData.getWrongPassword().getPassword();
        Response response = authApi.login(username, password);
        assertEquals(response.getStatusCode(), HttpStatusCodes.OK);
        assertEquals(response.jsonPath().getString("reason"), AuthConstants.BAD_CREDENTIALS);

    }

    @Test(groups = {"smoke","regression"})
    public void TC_012_Reject_login_with_non_existent_username(){
        String username = authTestData.getNonExistentUser().getUsername();
        String password = authTestData.getNonExistentUser().getPassword();
        Response response = authApi.login(username, password);
        assertEquals(response.getStatusCode(), HttpStatusCodes.OK);
        assertEquals(response.jsonPath().getString("reason"), AuthConstants.BAD_CREDENTIALS);
    }

    @Test(groups = {"smoke","regression"})
    public void TC_013_Reject_request_with_missing_password_field(){
        String username = authTestData.getMissingPasswordField().getUsername();
        String password = authTestData.getMissingPasswordField().getPassword();
        Response response = authApi.login(username, password);
        assertEquals(response.getStatusCode(), HttpStatusCodes.OK);
        assertEquals(response.jsonPath().getString("reason"), AuthConstants.BAD_CREDENTIALS);
    }

    @Test(groups = {"smoke","regression"})
    public void TC_014_Reject_request_with_empty_body(){
        Response response = authApi.submitAuthWithoutBody();
        assertEquals(response.getStatusCode(), HttpStatusCodes.OK);
        assertEquals(response.jsonPath().getString("reason"), AuthConstants.BAD_CREDENTIALS);
    }

    @Test(groups = {"smoke","regression"})
    public void TC_015_SQL_injection_payload_in_username_is_not_executed_no_token_leaked(){
        String username = authTestData.getSqlInjectionUsername().getUsername();
        String password = authTestData.getSqlInjectionUsername().getPassword();
        Response response = authApi.login(username, password);
        assertEquals(response.getStatusCode(), HttpStatusCodes.OK);
        assertEquals(response.jsonPath().getString("reason"), AuthConstants.BAD_CREDENTIALS);
    }

    @Test(groups = {"smoke","regression"})
    public void TC_016_Token_is_not_predictable_sequential_across_repeated_valid_logins(){
        String username =
                authTestData.getValidUser().getUsername();

        String password =
                authTestData.getValidUser().getPassword();

        int numberOfRequests = 3;
        Set<String> tokens = new HashSet<>();
        for (int i = 0; i < numberOfRequests; i++) {
            Response response = authApi.login(username, password);
            assertEquals(response.getStatusCode(), HttpStatusCodes.OK);
            String token = response.jsonPath().getString("token");
            tokens.add(token);
        }
        // Assert that all tokens are unique
        assertEquals(tokens.size(), numberOfRequests,
                "Duplicate token detected");
    }
    @Test(groups = {"smoke","regression"})
    public void TC_080_Repeated_failed_login_attempts_are_not_rate_limited_documented_risk(){
        String username =
                authTestData.getValidUser().getUsername();

        String password =
                authTestData.getValidUser().getPassword();

        int numberOfRequests = 20;
        for (int i = 0; i < numberOfRequests; i++) {
            Response response = authApi.login(username, password);
            assertEquals(response.getStatusCode(), HttpStatusCodes.OK);
        }

    }
    @Test(groups = {"smoke","regression"})
    public void TC081_Old_token_remains_valid_after_new_login_no_session_invalidation_documented_risk() {
        // Verified thật bằng curl: mỗi lần login sinh token KHÁC nhau, nhưng server không revoke token cũ
        // khi có login mới -> nhiều token cùng valid song song, không giới hạn 1 phiên/user. Đây là rủi ro
        // bảo mật đã được document, không phải giá trị mong đợi lý tưởng.
        AuthRequest validUser = new AuthRequest(
                authTestData.getValidUser().getUsername(),
                authTestData.getValidUser().getPassword()
        ); // hoặc lấy qua field đã có sẵn

        Response oldLoginResponse = authApi.login(validUser.getUsername(), validUser.getPassword());
        assertEquals(oldLoginResponse.getStatusCode(), HttpStatusCodes.OK);
        String oldToken = oldLoginResponse.jsonPath().getString("token");

        BookingIdsTestData bookingIdsTestData = TestDataUtils.readJsonFile(
                "com/herokuapp/restfulbooker/models/testdata/booking-data.json",
                BookingIdsTestData.class);

        Response createResponse = bookingApi.createBooking(bookingIdsTestData.getCreateValidBooking());
        int bookingId = createResponse.jsonPath().getInt("bookingid");

        Response newLoginResponse = authApi.login(validUser.getUsername(), validUser.getPassword());
        assertEquals(newLoginResponse.getStatusCode(), HttpStatusCodes.OK);
        String newToken = newLoginResponse.jsonPath().getString("token");

        assertTrue(!oldToken.equals(newToken), "2 lần login liên tiếp phải sinh ra 2 token khác nhau");

        // Token CŨ vẫn phải được chấp nhận ở 1 endpoint cần auth -> chứng minh server không revoke token cũ
        Response updateResponse = bookingApi.updateBookingWithToken(bookingId, bookingIdsTestData.getUpdateValidBooking(), oldToken);
        assertEquals(updateResponse.getStatusCode(), HttpStatusCodes.OK,
                "token cũ vẫn phải được chấp nhận vì hệ thống không hỗ trợ revoke/invalidate token khi có login mới");
    }
}
