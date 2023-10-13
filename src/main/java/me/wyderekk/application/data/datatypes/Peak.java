package me.wyderekk.application.data.datatypes;

import me.wyderekk.application.data.datatypes.enums.Division;
import me.wyderekk.application.data.datatypes.enums.Tier;

public record Peak(Tier tier, Division division, int lp, int wins, int loses, double winrate, long createdAt) {

}
