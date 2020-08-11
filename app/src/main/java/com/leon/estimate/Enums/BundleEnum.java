package com.leon.estimate.Enums;

/**
 * Created by Leon on 1/10/2018.
 */

public enum BundleEnum {
    BILL_ID("bill_Id"),
    TRACK_NUMBER("trackNumber"),
    NEW_ENSHEAB("new_ensheab"),
    DATA("data"),
    READ_STATUS("readStatus"),
    THEME("theme"),
    ACCOUNT("ACCOUNT"),
    TYPE("type"),
    ON_OFFLOAD("ON_OFFLOAD"),
    POSITION("position"),
    SPINNER_POSITION("spinner_position"),
    COUNTER_STATE_POSITION("counterStatePosition"),
    COUNTER_STATE_CODE("counterStatePosition"),
    NUMBER("counterStateCode"),
    CURRENT_PAGE("number"),
    IMAGE_BITMAP("image_bitmap"),
    REQUEST("request"),
    SERVICES("services");

    private final String value;

    BundleEnum(final String newValue) {
        value = newValue;
    }

    public String getValue() {
        return value;
    }
}
