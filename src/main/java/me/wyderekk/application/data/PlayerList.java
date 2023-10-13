package me.wyderekk.application.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class PlayerList {

    private final ArrayList<String> players = new ArrayList<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerList.class);
    public static final PlayerList INSTANCE = new PlayerList();

    private PlayerList() {
        addPlayers();
    }

    private void addPlayers() {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("META-INF/resources/frontend/playerlist.txt");
        if (inputStream != null) {
            try (Scanner sc = new Scanner(inputStream)) {
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    players.add(line);
                    LOGGER.info("Added {} to player list", line);
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
