package me.wyderekk.application.task.tasks;

import me.wyderekk.application.api.RiotAPI;
import me.wyderekk.application.data.PlayerList;
import me.wyderekk.application.data.database.SQLite;
import me.wyderekk.application.data.datatypes.AccountData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateRiotAPIData implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateRiotAPIData.class);

    @Override
    public void run() {
        LOGGER.info("Updating RiotAPI data...");
        PlayerList.INSTANCE.getPlayersWithoutLOLPros().forEach(player -> {
            String[] data = player.split(";");
            // save AccountData obtained through RiotAPI to temp table
            SQLite.saveAccountData(RiotAPI.getAccountData(data[0], data[1], true), SQLite.RIOTAPI_DATA_TABLE_NAME);
            // get AccountData with highest rank from table
            AccountData accountData = SQLite.getPeakAccountData(data[1]);
            // save AccountData to main table
            if(accountData != null) {
                SQLite.saveAccountData(accountData, SQLite.RIOTAPI_DATA_TABLE_NAME);
            }
        });
    }
}
