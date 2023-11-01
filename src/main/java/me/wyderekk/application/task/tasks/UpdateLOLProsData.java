package me.wyderekk.application.task.tasks;

import me.wyderekk.application.api.LOLProsAPI;
import me.wyderekk.application.data.PlayerList;
import me.wyderekk.application.data.database.SQLite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateLOLProsData implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateLOLProsData.class);

    @Override
    public void run() {
        LOGGER.info("Updating lolpros data...");
        PlayerList.INSTANCE.getPlayers().forEach(player -> {
            String json = LOLProsAPI.getPlayer(player);
            LOLProsAPI.getAccountData(json).forEach(account -> {
                SQLite.saveAccountData(account, SQLite.LOLPROS_DATA_TABLE_NAME);
            });
        });
    }
}