package com.herokuapp.restfulbooker.models.testdata;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IdCase {
    private Object id;
    // Object vì id có lúc là số (1), có lúc là chuỗi ("abc")
    @JsonProperty("Accept")
    private String Accept;      // tên field trùng key JSON "Accept"

    public IdCase() {
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getAccept() {
        return Accept;
    }

    public void setAccept(String accept) {
        Accept = accept;
    }
}
