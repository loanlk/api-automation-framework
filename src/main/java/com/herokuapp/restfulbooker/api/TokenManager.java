package com.herokuapp.restfulbooker.api;

import com.herokuapp.restfulbooker.config.Credentials;

public class TokenManager {
    // ThreadLocal thay vì static field dùng chung — mỗi thread (mỗi test class khi chạy parallel="classes")
    // có token/session riêng, không còn bị lẫn danh tính giữa các thread chạy song song.
    private static final ThreadLocal<String> token = new ThreadLocal<>();
    private TokenManager() {}

    public static String getToken() {
        if (token.get() == null) {
            token.set(fetchNewToken());
        }
        return token.get();
    }

    private static String fetchNewToken() {
        return new AuthApi()
                .login(Credentials.username(), Credentials.password())
                .then().statusCode(200).extract().path("token");
    }
}
