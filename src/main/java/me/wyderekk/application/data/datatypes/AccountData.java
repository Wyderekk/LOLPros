package me.wyderekk.application.data.datatypes;

import me.wyderekk.application.data.datatypes.enums.Position;

import java.util.ArrayList;

public record AccountData(String owner, String id, ArrayList<SummonerName> summonerNames, Position position, int avatarId, Rank rank, Peak peak) {

}
