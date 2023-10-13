package me.wyderekk.application.data.datatypes.enums;

import java.util.Arrays;

public enum SortBy {

    CURRENT_RANK("Current Rank"),
    PEAK("Peak"),
    WINRATE("Winrate"),
    GAMES("Games");

    private final String name;

    SortBy(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SortBy getByName(String name) {
        return Arrays.stream(SortBy.values())
                .filter(e -> e.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }
}