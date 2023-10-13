package me.wyderekk.application.data.datatypes;

import me.wyderekk.application.data.datatypes.enums.Position;

public record AccountData(String owner, String id, String accountName, Position position, int avatarId, Rank rank, Peak peak) {

}
