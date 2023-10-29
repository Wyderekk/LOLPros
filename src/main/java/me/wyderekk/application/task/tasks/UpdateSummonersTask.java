package me.wyderekk.application.task.tasks;

import com.squareup.okhttp.OkHttpClient;
import me.wyderekk.application.api.lolpros.APIWrapper;
import me.wyderekk.application.data.PlayerList;
import me.wyderekk.application.data.database.SQLite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateSummonersTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateSummonersTask.class);
    private static final OkHttpClient client = new OkHttpClient();

    @Override
    public void run() {
        LOGGER.info("Updating database...");
        PlayerList.INSTANCE.getPlayers().forEach(player -> {
            String json = APIWrapper.getPlayer(player, client);
            APIWrapper.getAccountData(json).forEach(SQLite::saveAccountData);
        });
    }
}