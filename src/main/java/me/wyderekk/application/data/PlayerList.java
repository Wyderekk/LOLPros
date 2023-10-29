package me.wyderekk.application.data;

import me.wyderekk.application.data.database.SQLite;
import me.wyderekk.application.data.datatypes.enums.Badge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class PlayerList {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerList.class);
    public static final PlayerList INSTANCE = new PlayerList();
    private final ArrayList<String> players = new ArrayList<>();

    private PlayerList() {
        addPlayers();
    }

    private void addPlayers() {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("META-INF/resources/frontend/playerlist.txt");
        if (inputStream != null) {
            try (Scanner sc = new Scanner(inputStream)) {
                while (sc.hasNextLine()) {
                    String[] line = sc.nextLine().split(";");
                    players.add(line[0]);
                    LOGGER.info("Added {} to player list", line[0]);
                    if (line.length == 2) {
                        SQLite.saveBadges(line[0], line[1]);
                    }
                }
            }
        } else {
            LOGGER.error("Player list file not found.");
        }
    }

    public ArrayList<String> getPlayers() {
        return players;
    }
}
