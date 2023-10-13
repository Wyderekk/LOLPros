package me.wyderekk.application.data.util;

import me.wyderekk.application.data.datatypes.enums.Division;
import me.wyderekk.application.data.datatypes.enums.Tier;

public class AccountDataUtil {

    public static String parse(Tier tier, Division division, int lp) {
        String tierName = tier.getName().equals("Unranked") ? tier.getName() : tier.getName() + " " + division.ordinal();
        String lpString = tierName.equals("Unranked") ? "" : " - " + lp + "LP";
        return tierName + lpString;
    }

    public static double getRoundedWinrate(double winrate) {
        return Math.round(winrate * 1000.0) / 10.0;
    }

    public static int getRoundedWinrateAsInt(double winrate) {
        return (int) Math.round(winrate * 1000) / 10;
    }
}
