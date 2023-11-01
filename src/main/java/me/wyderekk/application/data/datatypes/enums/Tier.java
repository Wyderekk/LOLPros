package me.wyderekk.application.data.datatypes.enums;

import java.util.Arrays;

public enum Tier {

    UNRANKED("Unranked"),
    IRON("Iron"),
    BRONZE("Bronze"),
    SILVER("Silver"),
    GOLD("Gold"),
    PLATINUM("Platinum"),
    EMERALD("Emerald"),
    DIAMOND("Diamond"),
    MASTER("Master"),
    GRANDMASTER("Grandmaster"),
    CHALLENGER("Challenger");

    private final String name;

    Tier(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public static Tier getByName(String name) {
        String[] tier = name.split("_");
        return Arrays.stream(Tier.values())
                .filter(e -> e.name().equalsIgnoreCase(tier.length == 2 ? tier[1] : name))
                .findAny()
                .orElse(null);
    }
}
