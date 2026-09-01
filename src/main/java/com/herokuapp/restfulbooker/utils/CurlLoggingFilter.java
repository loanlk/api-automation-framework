package com.herokuapp.restfulbooker.utils;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class CurlLoggingFilter implements Filter {
    private static final ThreadLocal<String> LAST_CURL = new ThreadLocal<>();

    public static String getLastCurl() {
        return LAST_CURL.get();
    }

    public static void clear() {          // thêm để dọn ThreadLocal, tránh rò rỉ khi chạy song song
        LAST_CURL.remove();
    }

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        StringBuilder curl = new StringBuilder("curl -X ")
                .append(requestSpec.getMethod())
                .append(" '").append(requestSpec.getURI()).append("'");

        requestSpec.getHeaders().forEach(h -> {
            // che token/cookie để không lộ trong log
            String value = ("Authorization".equalsIgnoreCase(h.getName())
                    || "Cookie".equalsIgnoreCase(h.getName()))
                    ? "***REDACTED***" : h.getValue();
            curl.append(" -H '").append(h.getName()).append(": ").append(value).append("'");
        });

        if (requestSpec.getBody() != null) {
            curl.append(" -d '").append(requestSpec.getBody().toString()).append("'");
        } else if (!requestSpec.getFormParams().isEmpty()) {
            // .formParam(...) không đi qua getBody() — REST Assured chỉ encode thành body lúc gửi request
            String encoded = requestSpec.getFormParams().entrySet().stream()
                    .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8)
                            + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&"));
            curl.append(" -d '").append(encoded).append("'");
        }

        String curlStr = curl.toString();
        LAST_CURL.set(curlStr);
        // Không in ra console ở đây nữa — làm vậy sẽ ồn ào ngay cả khi request pass, chôn lẫn request
        // thật sự gây fail giữa hàng trăm dòng log. Việc in console giờ chỉ xảy ra khi test fail, xem
        // CurlOnFailureListener. Muốn xem curl của MỌI request khi debug local (không quan tâm pass/fail):
        // chạy với -Dcurl.verbose=true
        if (Boolean.getBoolean("curl.verbose")) {
            System.out.println("[CURL] " + curlStr);
        }
        return ctx.next(requestSpec, responseSpec);
    }

}

