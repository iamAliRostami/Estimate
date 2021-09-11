package com.leon.estimate.Enums;

/**
 * Created by Leon on 1/9/2018.
 */

public enum SharedReferenceKeys {
    USERNAME_TEMP("username_temp"),
    PASSWORD_TEMP("password_temp"),
    USERNAME("username"),
    PASSWORD("password"),
    TOKEN("token"),
    TOKEN_FOR_FILE("token_for_file"),
    REFRESH_TOKEN("refresh_token"),
    TRACK_NUMBER("track_number");

    private final String value;

    SharedReferenceKeys(final String newValue) {
        value = newValue;
    }

    public String getValue() {
        return value;
    }
}
