package com.leon.estimate.Enums;

/**
 * Created by Leon on 12/28/2017.
 */

public enum OffloadStateEnum {
    INSERTED(0),
    REGISTERD(2),
    SENT(4),
    ARCHIVED(8),
    LOGICAL_DELETED(16),
    DELETED(32),
    SENT_WITH_ERROR(64);

    private final int value;

    OffloadStateEnum(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
