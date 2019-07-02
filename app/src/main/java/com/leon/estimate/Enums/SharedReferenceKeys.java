package com.leon.estimate.Enums;

/**
 * Created by Leon on 1/9/2018.
 */

public enum SharedReferenceKeys {
    USERNAME("username"),
    PASSWORD("password"),
    TOKEN("token"),
    REFRESH_TOKEN("refresh_token"),
    THEME_STABLE("theme_stable"),
    THEME_TEMPORARY("theme_temporary"),
    BILL_COUNTER("bill_counter");

    private final String value;

    SharedReferenceKeys(final String newValue) {
        value = newValue;
    }

    public String getValue() {
        return value;
    }
}
