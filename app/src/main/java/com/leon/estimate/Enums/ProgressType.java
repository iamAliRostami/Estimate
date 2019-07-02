package com.leon.estimate.Enums;

/**
 * Created by Leon on 1/17/2018.
 */

public enum ProgressType {
    SHOW(1),
    SHOW_CANCELABLE(2),
    NOT_SHOW(3);

    private final int value;

    ProgressType(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
