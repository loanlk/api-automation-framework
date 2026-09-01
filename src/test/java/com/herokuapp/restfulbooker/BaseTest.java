package com.herokuapp.restfulbooker;

import com.herokuapp.restfulbooker.api.AuthApi;
import com.herokuapp.restfulbooker.api.BookingApi;
import com.herokuapp.restfulbooker.api.HealthApi;
import com.herokuapp.restfulbooker.api.TokenManager;
import com.herokuapp.restfulbooker.models.testdata.AuthTestData;
import com.herokuapp.restfulbooker.models.testdata.BookingIdsTestData;
import com.herokuapp.restfulbooker.utils.CurlLoggingFilter;
import com.herokuapp.restfulbooker.utils.TestDataUtils;
import io.qameta.allure.Allure;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.herokuapp.restfulbooker.utils.RetryUtils.retryUntilSuccess;
@Listeners(com.herokuapp.restfulbooker.listeners.CurlOnFailureListener.class)
public class BaseTest {
    protected AuthApi authApi;
    protected BookingApi bookingApi;
    protected HealthApi healthApi;
    private String cachedToken;
    private String cachedBasicAuthCredentials;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        authApi = new AuthApi();
        bookingApi = new BookingApi();
        healthApi = new HealthApi();
    }

    // Đính curl của request gần nhất (gọi ngay sau 1 lệnh API trong @BeforeClass/@BeforeMethod)
    protected void attachCurlEvidence(String label) {
        String curl = CurlLoggingFilter.getLastCurl();
        if (curl != null) {
            Allure.addAttachment(label + " - curl", "text/plain", curl, ".txt");
        }
    }

    // Đính "giá trị trả về" của 1 fixture method (vd bookingId vừa tạo, token vừa lấy)
    protected void attachReturnValue(String label, Object value) {
        Allure.addAttachment(label + " - return value", "text/plain", String.valueOf(value), ".txt");
    }

    // Đính nguyên nội dung JSON test data vừa load được, để thấy rõ "trả về" gì từ @BeforeClass
    protected void attachLoadedJson(String label, String classpathPath) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(classpathPath)) {
            if (is != null) {
                String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Allure.addAttachment(label, "application/json", content, ".json");
            }
        } catch (IOException ignored) {
        }
    }

    private AuthTestData loadAuthTestData() {
        return TestDataUtils.readJsonFile(
                "com/herokuapp/restfulbooker/models/testdata/auth-data.json",
                AuthTestData.class
        );
    }

    protected String getAuthToken() {
        return TokenManager.getToken();
    }

    // Dùng cho auth kiểu Basic: base64(username:password), không cần gọi API
    protected String getBasicAuthCredentials() {
        if (cachedBasicAuthCredentials == null) {
            AuthTestData authTestData = loadAuthTestData();
            String raw = authTestData.getValidUser().getUsername()+ ":" + authTestData.getValidUser().getPassword();
            cachedBasicAuthCredentials = Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
        }
        return cachedBasicAuthCredentials;
    }

    public int createBooking(BookingIdsTestData bookingIdsTestData) {

        int bookingId = (int)bookingApi.createBooking(bookingIdsTestData.getCreateValidBooking())
                .then().log().all()
                .extract().path("bookingid");
        attachCurlEvidence("createBookingForUpdate - create booking");
        attachReturnValue("createBookingForUpdate - bookingId", bookingId);
        return bookingId;
    }
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        CurlLoggingFilter.clear();
    }
}
