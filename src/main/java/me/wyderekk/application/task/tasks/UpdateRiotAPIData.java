package me.wyderekk.application.task.tasks;

import me.wyderekk.application.api.RiotAPI;
import me.wyderekk.application.data.PlayerList;
import me.wyderekk.application.data.database.SQLite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateRiotAPIData implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateRiotAPIData.class);

    @Override
    public void run() {
        LOGGER.info("Updating RiotAPI data...");
        PlayerList.INSTANCE.getPlayersWithoutLOLPros().forEach(player -> {
            // split each line from riotapi.txt into owner and puuid
            String[] data = player.split(";");
            // save AccountData obtained through RiotAPI to temp table
            SQLite.saveAccountData(RiotAPI.getAccountData(data[0], data[1], true), SQLite.RIOTAPI_DATA_TABLE_NAME);
            SQLite.savePeakAccountData(data[1]);
        });
    }
}
