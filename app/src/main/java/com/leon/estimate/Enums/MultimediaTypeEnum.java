package com.leon.estimate.Enums;

/**
 * Created by Leon on 1/9/2018.
 */

public enum MultimediaTypeEnum {
    AUDIO("1"),
    IMAGE("2"),
    VIDEO("3"),
    TEXT("4");
    private final String value;

    MultimediaTypeEnum(final String newValue) {
        value = newValue;
    }

    public String getValue() {
        return value;
    }
}
