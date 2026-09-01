package com.herokuapp.restfulbooker.models.response;

import com.herokuapp.restfulbooker.models.Booking;

public class CreateBookingResponse {
    private int bookingid;
    private Booking booking;

    public CreateBookingResponse() {
    }

    public int getBookingid() {
        return bookingid;
    }

    public void setBookingid(int bookingid) {
        this.bookingid = bookingid;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

}
