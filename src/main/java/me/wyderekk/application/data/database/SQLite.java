package me.wyderekk.application.data.database;

import me.wyderekk.application.data.dao.AccountDataDao;
import me.wyderekk.application.data.datatypes.AccountData;
import me.wyderekk.application.data.datatypes.comparator.AccountDataComparator;
import me.wyderekk.application.data.datatypes.enums.Badge;
import me.wyderekk.application.data.datatypes.enums.SortBy;
import me.wyderekk.application.task.TaskRunner;
import me.wyderekk.application.task.tasks.UpdateSummonersTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class SQLite {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLite.class);
    private static Connection con;

    public static void connect() {
        File databaseFile = new File("database.db");
        String URL = "jdbc:sqlite:" + databaseFile;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection(URL);
            createTable();
            TaskRunner.runInBackground(new UpdateSummonersTask(), 1, 3, TimeUnit.HOURS);
            hookDisconnect();
            LOGGER.info("Connected to database");
        } catch (Exception e) {
            LOGGER.error("Failed to connect to database", e);
        }
    }

    private static void hookDisconnect() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if(con != null && !con.isClosed()) {
                    con.close();
                    LOGGER.info("Disconnected from database");
                }
            } catch (SQLException e) {
                LOGGER.error("Failed to disconnect from database", e);
            }
        }));
    }

    private static void createTable() {
        String SQL =
                """
                CREATE TABLE IF NOT EXISTS account_data (
                    id TEXT PRIMARY KEY,
                    json_data TEXT
                );
                """;
        String SQL2 =
                """
                CREATE TABLE IF NOT EXISTS badges (
                    owner TEXT PRIMARY KEY,
                    badges TEXT
                );
                """;
        try {
            con.createStatement().execute(SQL);
            con.createStatement().execute(SQL2);
        } catch (SQLException e) {
            LOGGER.error("Failed to create table", e);
        }
    }

    public static void saveAccountData(AccountData accountData) {
        String SQL = accountDataExists(accountData.id()) ? "REPLACE INTO account_data (id, json_data) VALUES (?, ?)" : "INSERT INTO account_data (id, json_data) VALUES (?, ?)";
        String jsonString = AccountDataDao.toJsonString(accountData);

        try (PreparedStatement updateStatement = con.prepareStatement(SQL)) {
            updateStatement.setString(1, accountData.id());
            updateStatement.setString(2, jsonString);
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Failed to save account data", e);
        }
    }

    public static ArrayList<AccountData> getAccountData(String owner) {
        String SQL = "SELECT * FROM account_data WHERE json_extract(json_data, '$.owner') COLLATE NOCASE = ?";
        ArrayList<AccountData> accountDataArrayList = new ArrayList<>();
        try (PreparedStatement preparedStatement = con.prepareStatement(SQL)) {
            preparedStatement.setString(1, owner);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String jsonData = resultSet.getString("json_data");
                    accountDataArrayList.add(AccountDataDao.fromJsonString(jsonData));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to get account data", e);
        }

        Comparator<AccountData> comparator = AccountDataComparator.getComparator(SortBy.CURRENT_RANK);
        accountDataArrayList.sort(comparator);
        LOGGER.info("Returning {} account data for {}", accountDataArrayList.size(), owner);
        return accountDataArrayList;
    }

    public static ArrayList<AccountData> getSortedAccountData(SortBy sortBy) {
        String SQL = "SELECT * FROM account_data";

        try (PreparedStatement preparedStatement = con.prepareStatement(SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            ArrayList<AccountData> accountDataArrayList = new ArrayList<>();

            while (resultSet.next()) {
                String jsonData = resultSet.getString("json_data");
                accountDataArrayList.add(AccountDataDao.fromJsonString(jsonData));
            }

            Comparator<AccountData> comparator = AccountDataComparator.getComparator(sortBy);
            accountDataArrayList.sort(comparator);

            return accountDataArrayList;
        } catch (SQLException e) {
            LOGGER.error("Failed to get sorted account data", e);
        }
        return null;
    }

    private static boolean accountDataExists(String accountId) {
        String checkSQL = "SELECT COUNT(*) FROM account_data WHERE id = ?";

        try (PreparedStatement checkStatement = con.prepareStatement(checkSQL)) {
            checkStatement.setString(1, accountId);
            try (ResultSet resultSet = checkStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to check if account data exists", e);
        }
        return false;
    }

    public static void saveBadges(String owner, String badges) {
        String SQL = badgesExist(owner) ? "REPLACE INTO badges (owner, badges) VALUES (?, ?)" : "INSERT INTO badges (owner, badges) VALUES (?, ?)";
        try (PreparedStatement updateStatement = con.prepareStatement(SQL)) {
            updateStatement.setString(1, owner);
            updateStatement.setString(2, badges);
            updateStatement.executeUpdate();
            LOGGER.info("Saved badges for {}", owner);
        } catch (SQLException e) {
            LOGGER.error("Failed to save badges", e);
        }
    }

    private static boolean badgesExist(String owner) {
        String checkSQL = "SELECT COUNT(*) FROM badges WHERE owner = ?";

        try (PreparedStatement checkStatement = con.prepareStatement(checkSQL)) {
            checkStatement.setString(1, owner);
            try (ResultSet resultSet = checkStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to check if badges exist", e);
        }
        return false;
    }

    public static ArrayList<Badge> getBadges(String owner) {
        String SQL = "SELECT * FROM badges WHERE owner = ?";
        ArrayList<Badge> badges = new ArrayList<>();
        try (PreparedStatement preparedStatement = con.prepareStatement(SQL)) {
            preparedStatement.setString(1, owner);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String badgesString = resultSet.getString("badges").toUpperCase();
                    LOGGER.info("Returning badges {} for {}", badgesString, owner);
                    for (String badge : badgesString.split(",")) {
                        badges.add(Badge.valueOf(badge));
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to get badges", e);
        }
        return badges;
    }
}