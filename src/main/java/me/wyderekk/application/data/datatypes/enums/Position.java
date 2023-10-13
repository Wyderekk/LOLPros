package me.wyderekk.application.data.datatypes.enums;

import java.util.Arrays;

public enum Position {

    TOP("Top"),
    JUNGLE("Jungle"),
    MID("Mid"),
    ADC("ADC"),
    SUPPORT("Support");

    private final String name;

    Position(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Position getPosition(String name) {
        return Arrays.stream(Position.values())
                .filter(e -> e.name().equalsIgnoreCase(name.split("_")[1])).findAny().orElse(null);
    }
}
