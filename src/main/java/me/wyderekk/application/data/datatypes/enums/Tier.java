package me.wyderekk.application.data.datatypes.enums;

import java.util.Arrays;

public enum Tier {

    CHALLENGER("Challenger"),
    GRANDMASTER("Grandmaster"),
    MASTER("Master"),
    DIAMOND("Diamond"),
    EMERALD("Emerald"),
    PLATINUM("Platinum"),
    GOLD("Gold"),
    SILVER("Silver"),
    BRONZE("Bronze"),
    IRON("Iron"),
    UNRANKED("Unranked");

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
