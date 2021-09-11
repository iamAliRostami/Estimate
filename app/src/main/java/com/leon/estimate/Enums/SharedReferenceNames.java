package com.leon.estimate.Enums;

/**
 * Created by Leon on 1/10/2018.
 */

public enum SharedReferenceNames {
    THEME("com.app.leon.abfa.theme_info"),
    ACCOUNT("com.app.leon.abfa.account_info");


    private final String value;

    SharedReferenceNames(final String newValue) {
        value = newValue;
    }

    public String getValue() {
        return value;
    }
}
