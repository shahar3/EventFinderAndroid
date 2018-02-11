package com.example.shaha.eventfinderandroid.Utils;

/**
 * Created by shaha on 10/02/2018.
 */

public enum EventType {
    SPORT(1),
    CARPOOL(2),
    SALE(3),
    PARTY(4),
    BAR_SALE(5),
    MISC(6);

    private final int value;
    private EventType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
