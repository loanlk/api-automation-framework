package com.herokuapp.restfulbooker.models.testdata;

import com.herokuapp.restfulbooker.models.Booking;
import com.herokuapp.restfulbooker.models.BookingFilter;

import java.util.Map;

public class BookingIdsTestData {
    private BookingFilter filterByFirstnameLastname;
    private BookingFilter filterCheckinCheckout;
    private BookingFilter filterNameNoBookingFound;
    private Map<String, Object> filterMalformedDate;
    private Map<String, Object> filterXSSPayloadInfirstname;

    private BookingWithMeta validID;
    private BookingWithMeta validIDinXML;
    private IdCase notExist;
    private IdCase nonNumericID;
    private IdCase validIDinAppPdf;

    private Booking createValidBooking;
    private Booking createMissingFirstnameRequired;
    private Map<String,Object> createWrongPriceDataType;

    private Booking createCheckoutDateprecedesCheckinDate;
    private Booking createInvalidCheckinCheckoutDates;
    private Booking createNegativeTotalPrice;
    private BookingWithMeta createValidBookingInXML;
    private BookingWithMeta createValidBookingInFormUrlEncoded;
    private Booking createSQLinjectFirstname;
    private BookingWithMeta createBookingWithVeryLongStringValues;

    private Booking updateValidBooking;
    private BookingWithMeta updateValidUpdateBookingWithInvalidToken;
    private BookingWithMeta updateValidUpdateBookingWithInvalidBookingId;
    private Booking updateMissingLastname;

    public Map<String, Object> getPartialUpdateValidBooking() {
        return partialUpdateValidBooking;
    }

    public void setPartialUpdateValidBooking(Map<String,Object> partialUpdateValidBooking) {
        this.partialUpdateValidBooking = partialUpdateValidBooking;
    }

    private Map<String,Object> partialUpdateValidBooking;
    private Map<String,Object> partialUpdateWithInvalidFieldType;

    public IdCase getDeleteWithNotExistingBookingId() {
        return deleteWithNotExistingBookingId;
    }

    public void setDeleteWithNotExistingBookingId(IdCase deleteWithNotExistingBookingId) {
        this.deleteWithNotExistingBookingId = deleteWithNotExistingBookingId;
    }

    private IdCase deleteWithNotExistingBookingId;

    public Map<String,Object> getPartialUpdateWithInvalidFieldType() {
        return partialUpdateWithInvalidFieldType;
    }

    public void setPartialUpdateWithInvalidFieldType(Map<String,Object> partialUpdateWithInvalidFieldType) {
        this.partialUpdateWithInvalidFieldType = partialUpdateWithInvalidFieldType;
    }

    public Booking getUpdateMissingLastname() {
        return updateMissingLastname;
    }

    public void setUpdateMissingLastname(Booking updateMissingLastname) {
        this.updateMissingLastname = updateMissingLastname;
    }

    public Booking getUpdateValidBooking() {
        return updateValidBooking;
    }

    public void setUpdateWithValidBooking(Booking updateWithValidBooking) {
        this.updateValidBooking = updateWithValidBooking;
    }

    public BookingWithMeta getUpdateValidUpdateBookingWithInvalidToken() {
        return updateValidUpdateBookingWithInvalidToken;
    }

    public void setUpdateValidUpdateBookingWithInvalidToken(BookingWithMeta updateValidUpdateBookingWithInvalidToken) {
        this.updateValidUpdateBookingWithInvalidToken = updateValidUpdateBookingWithInvalidToken;
    }

    public BookingWithMeta getUpdateValidUpdateBookingWithInvalidBookingId() {
        return updateValidUpdateBookingWithInvalidBookingId;
    }

    public void setUpdateValidUpdateBookingWithInvalidBookingId(BookingWithMeta updateValidUpdateBookingWithInvalidBookingId) {
        this.updateValidUpdateBookingWithInvalidBookingId = updateValidUpdateBookingWithInvalidBookingId;
    }

    public Booking getCreateValidBooking() {
        return createValidBooking;
    }

    public void setCreateValidBooking(Booking createValidBooking) {
        this.createValidBooking = createValidBooking;
    }

    public Booking getCreateMissingFirstnameRequired() {
        return createMissingFirstnameRequired;
    }

    public void setCreateMissingFirstnameRequired(Booking createMissingFirstnameRequired) {
        this.createMissingFirstnameRequired = createMissingFirstnameRequired;
    }

    public Map<String, Object> getCreateWrongPriceDataType() {
        return createWrongPriceDataType;
    }

    public void setCreateWrongPriceDataType(Map<String,Object> createWrongPriceDataType) {
        this.createWrongPriceDataType = createWrongPriceDataType;
    }

    public Booking getCreateCheckoutDateprecedesCheckinDate() {
        return createCheckoutDateprecedesCheckinDate;
    }

    public void setCreateCheckoutDateprecedesCheckinDate(Booking createCheckoutDateprecedesCheckinDate) {
        this.createCheckoutDateprecedesCheckinDate = createCheckoutDateprecedesCheckinDate;
    }

    public Booking getCreateInvalidCheckinCheckoutDates() {
        return createInvalidCheckinCheckoutDates;
    }

    public void setCreateInvalidCheckinCheckoutDates(Booking createInvalidCheckinCheckoutDates) {
        this.createInvalidCheckinCheckoutDates = createInvalidCheckinCheckoutDates;
    }

    public Booking getCreateNegativeTotalPrice() {
        return createNegativeTotalPrice;
    }

    public void setCreateNegativeTotalPrice(Booking createNegativeTotalPrice) {
        this.createNegativeTotalPrice = createNegativeTotalPrice;
    }

    public BookingWithMeta getCreateValidBookingInXML() {
        return createValidBookingInXML;
    }

    public void setCreateValidBookingInXML(BookingWithMeta createValidBookingInXML) {
        this.createValidBookingInXML = createValidBookingInXML;
    }

    public BookingWithMeta getCreateValidBookingInFormUrlEncoded() {
        return createValidBookingInFormUrlEncoded;
    }

    public void setCreateValidBookingInFormUrlEncoded(BookingWithMeta createValidBookingInFormUrlEncoded) {
        this.createValidBookingInFormUrlEncoded = createValidBookingInFormUrlEncoded;
    }

    public Booking getCreateSQLinjectFirstname() {
        return createSQLinjectFirstname;
    }

    public void setCreateSQLinjectFirstname(Booking createSQLinjectFirstname) {
        this.createSQLinjectFirstname = createSQLinjectFirstname;
    }

    public BookingWithMeta getCreateBookingWithVeryLongStringValues() {
        return createBookingWithVeryLongStringValues;
    }

    public void setCreateBookingWithVeryLongStringValues(BookingWithMeta createBookingWithVeryLongStringValues) {
        this.createBookingWithVeryLongStringValues = createBookingWithVeryLongStringValues;
    }

    public BookingFilter getFilterByFirstnameLastname() {
        return filterByFirstnameLastname;
    }

    public void setFilterByFirstnameLastname(BookingFilter filterByFirstnameLastname) {
        this.filterByFirstnameLastname = filterByFirstnameLastname;
    }

    public BookingFilter getFilterCheckinCheckout() {
        return filterCheckinCheckout;
    }

    public void setFilterCheckinCheckout(BookingFilter filterCheckinCheckout) {
        this.filterCheckinCheckout = filterCheckinCheckout;
    }

    public BookingFilter getFilterNameNoBookingFound() {
        return filterNameNoBookingFound;
    }

    public void setFilterNameNoBookingFound(BookingFilter filterNameNoBookingFound) {
        this.filterNameNoBookingFound = filterNameNoBookingFound;
    }

    public Map<String, Object> getFilterMalformedDate() {
        return filterMalformedDate;
    }

    public void setFilterMalformedDate(Map<String, Object> filterMalformedDate) {
        this.filterMalformedDate = filterMalformedDate;
    }

    public Map<String, Object> getFilterXSSPayloadInfirstname() {
        return filterXSSPayloadInfirstname;
    }

    public void setFilterXSSPayloadInfirstname(Map<String, Object> filterXSSPayloadInfirstname) {
        this.filterXSSPayloadInfirstname = filterXSSPayloadInfirstname;
    }

    public BookingWithMeta getValidID() {
        return validID;
    }

    public void setValidID(BookingWithMeta validID) {
        this.validID = validID;
    }

    public BookingWithMeta getValidIDinXML() {
        return validIDinXML;
    }

    public void setValidIDinXML(BookingWithMeta validIDinXML) {
        this.validIDinXML = validIDinXML;
    }

    public IdCase getNotExist() {
        return notExist;
    }

    public void setNotExist(IdCase notExist) {
        this.notExist = notExist;
    }

    public IdCase getNonNumericID() {
        return nonNumericID;
    }

    public void setNonNumericID(IdCase nonNumericID) {
        this.nonNumericID = nonNumericID;
    }

    public IdCase getValidIDinAppPdf() {
        return validIDinAppPdf;
    }

    public void setValidIDinAppPdf(IdCase validIDinAppPdf) {
        this.validIDinAppPdf = validIDinAppPdf;
    }

}
