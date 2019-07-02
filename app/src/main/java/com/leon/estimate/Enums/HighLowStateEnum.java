package com.leon.estimate.Enums;

/**
 * Created by Leon on 12/28/2017.
 */

public enum HighLowStateEnum {
    NORMAL(0),
    HIGH(2),
    LOW(4),
    ZERO(8),
    UN_CALCULATED(16);

    private final int value;

    HighLowStateEnum(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
