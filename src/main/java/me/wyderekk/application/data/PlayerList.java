package me.wyderekk.application.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class PlayerList {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerList.class);
    public static final PlayerList INSTANCE = new PlayerList();
    private final ArrayList<String> players = new ArrayList<>();
    private final ArrayList<String> playersWithoutLOLPros = new ArrayList<>();

    private PlayerList() {
        addPlayers();
    }

    private void addPlayers() {
        ClassLoader classLoader = getClass().getClassLoader();

        InputStream inputStream = classLoader.getResourceAsStream("META-INF/resources/frontend/lolpros.txt");
        readDataFromInputStream(inputStream, players);

        inputStream = classLoader.getResourceAsStream("META-INF/resources/frontend/riotapi.txt");
        readDataFromInputStream(inputStream, playersWithoutLOLPros);
    }

    private void readDataFromInputStream(InputStream inputStream, ArrayList<String> arrayList) {
        if (inputStream != null) {
            try (Scanner sc = new Scanner(inputStream)) {
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    arrayList.add(line);
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

    public ArrayList<String> getPlayersWithoutLOLPros() {
        return playersWithoutLOLPros;
    }
}
