package com.herokuapp.restfulbooker.models;

import java.util.HashMap;
import java.util.Map;

public class BookingFilter {
    private String firstname;
    private String lastname;
    private String checkin;
    private String checkout;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getCheckin() {
        return checkin;
    }

    public void setCheckin(String checkin) {
        this.checkin = checkin;
    }

    public String getCheckout() {
        return checkout;
    }

    public void setCheckout(String checkout) {
        this.checkout = checkout;
    }

    public BookingFilter() {
    }
    public Map<String, Object> toQueryMap() {
        Map<String, Object> map = new HashMap<>();
        if (firstname != null) map.put("firstname", firstname);
        if (lastname != null) map.put("lastname", lastname);
        if (checkin != null) map.put("checkin", checkin);
        if (checkout != null) map.put("checkout", checkout);
        return map;
    }
}
