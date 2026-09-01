package com.herokuapp.restfulbooker.constants;

public class HttpStatusCodes {
    private HttpStatusCodes(){}
    public static final int OK = 200;
    public static final int CREATED = 201;
    public static final int BAD_REQUEST = 400;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int GONE_OR_INVALID_ID = 405;
    public static final int TEAPOT_UNSUPPORTED_ACCEPT = 418;
    public static final int VALIDATION_ERROR = 500;

}
