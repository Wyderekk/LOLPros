package me.wyderekk.application.data.datatypes.enums;

import java.util.Arrays;

public enum Division {

    UNRANKED,
    I,
    II,
    III,
    IV,
    V;

    public static Division getByValue(int value) {
        return Arrays.stream(Division.values())
                .filter(e -> e.ordinal() == value).findAny().orElse(null);
    }
}
