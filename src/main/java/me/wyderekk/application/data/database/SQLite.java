package me.wyderekk.application.data.database;

import me.wyderekk.application.data.dao.AccountDataDao;
import me.wyderekk.application.data.datatypes.AccountData;
import me.wyderekk.application.data.datatypes.Rank;
import me.wyderekk.application.data.datatypes.comparators.AccountDataComparator;
import me.wyderekk.application.data.datatypes.enums.Badge;
import me.wyderekk.application.data.datatypes.enums.SortBy;
import me.wyderekk.application.task.TaskRunner;
import me.wyderekk.application.task.tasks.UpdateLOLProsData;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SQLite {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLite.class);
    private static Connection con;

    public static final String LOLPROS_DATA_TABLE_NAME = "lolpros_data";
    public static final String RIOTAPI_DATA_TABLE_NAME = "riotapi_data";
    public static final String BADGES_TABLE_NAME = "user_badges";

    public static void connect() {
        File databaseFile = new File("database.db");
        String URL = "jdbc:sqlite:" + databaseFile;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection(URL);
            createTable();
            TaskRunner.runInBackground(1, 3, TimeUnit.HOURS, new UpdateLOLProsData(), new UpdateLOLProsData());
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
        String SQL1 = """
                CREATE TABLE IF NOT EXISTS %s (
                    id TEXT PRIMARY KEY,
                    json_data TEXT
                );
                """.formatted(LOLPROS_DATA_TABLE_NAME);

        String SQL2 = """
                CREATE TABLE IF NOT EXISTS %s (
                    owner TEXT PRIMARY KEY,
                    badges TEXT
                );
                """.formatted(BADGES_TABLE_NAME);

        String SQL3 = """
                CREATE TABLE IF NOT EXISTS %s (
                    id TEXT PRIMARY KEY,
                    json_data TEXT
                );
                """.formatted(RIOTAPI_DATA_TABLE_NAME);

        try (Statement stmt = con.createStatement()) {
            stmt.execute(SQL1);
            stmt.execute(SQL2);
            stmt.execute(SQL3);
        } catch (SQLException e) {
            LOGGER.error("Failed to create table", e);
        }
    }

    public static void saveAccountData(AccountData accountData, String tableName) {
        String SQL = accountDataExists(accountData.id()) ? "REPLACE INTO " + tableName + " (id, json_data) VALUES (?, ?)" : "INSERT INTO " + tableName + " (id, json_data) VALUES (?, ?)";
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
        String SQL = "SELECT * FROM " + LOLPROS_DATA_TABLE_NAME + " WHERE json_extract(json_data, '$.owner') COLLATE NOCASE = ?";

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
        String SQL = "SELECT * FROM " + LOLPROS_DATA_TABLE_NAME;

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

    public static AccountData getPeakAccountData(String accountId) {
        String SQL = "SELECT COUNT(*) FROM " + RIOTAPI_DATA_TABLE_NAME + " WHERE id = ?";

        try (PreparedStatement preparedStatement = con.prepareStatement(SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            preparedStatement.setString(1, accountId);
            ArrayList<AccountData> accountDataArrayList = new ArrayList<>();

            while (resultSet.next()) {
                String jsonData = resultSet.getString("json_data");
                accountDataArrayList.add(AccountDataDao.fromJsonString(jsonData));
            }

            Comparator<AccountData> comparator = AccountDataComparator.getComparator(SortBy.CURRENT_RANK);
            accountDataArrayList.sort(comparator);

            Rank peak = accountDataArrayList.getFirst().rank();

            accountDataArrayList.sort(Comparator.comparingLong(o -> o.rank().createdAt()));

            AccountData accountData = accountDataArrayList.getLast();

            clearTempData(accountData.owner());

            AccountData returnAccountData = new AccountData(
                    accountData.owner(),
                    accountData.id(),
                    accountData.summonerNames(),
                    accountData.position(),
                    accountData.avatarId(),
                    accountData.rank(),
                    peak
            );

            saveAccountData(accountData, RIOTAPI_DATA_TABLE_NAME);

            return returnAccountData;
        } catch (SQLException e) {
            LOGGER.error("Failed to get sorted account data", e);
        }
        return null;
    }


    private static boolean accountDataExists(String accountId) {
        String SQL = "SELECT COUNT(*) FROM " + LOLPROS_DATA_TABLE_NAME + " WHERE id = ?";

        try (PreparedStatement checkStatement = con.prepareStatement(SQL)) {
            checkStatement.setString(1, accountId);
            try (ResultSet resultSet = checkStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to check if account data exists", e);
        }
        return false;
    }

    public static void saveBadges(String owner, Badge... badges) {
        String SQL = badgesExist(owner) ? "REPLACE INTO " + BADGES_TABLE_NAME + " (owner, badges) VALUES (?, ?)" : "INSERT INTO " + BADGES_TABLE_NAME + " (owner, badges) VALUES (?, ?)";

        try (PreparedStatement updateStatement = con.prepareStatement(SQL)) {
            updateStatement.setString(1, owner);
            updateStatement.setString(2, Arrays.stream(badges).map(Enum::name).collect(Collectors.joining(",")));
            updateStatement.executeUpdate();
            LOGGER.info("Saved badges for {}", owner);
        } catch (SQLException e) {
            LOGGER.error("Failed to save badges", e);
        }
    }

    public static void saveBadges(String owner, ArrayList<Badge> badgeList) {
        String SQL = badgesExist(owner) ? "REPLACE INTO " + BADGES_TABLE_NAME + " (owner, badges) VALUES (?, ?)" : "INSERT INTO " + BADGES_TABLE_NAME + " (owner, badges) VALUES (?, ?)";

        try (PreparedStatement updateStatement = con.prepareStatement(SQL)) {
            updateStatement.setString(1, owner);
            updateStatement.setString(2, badgeList.stream().map(Enum::name).collect(Collectors.joining(",")));
            updateStatement.executeUpdate();
            LOGGER.info("Saved badges for {}", owner);
        } catch (SQLException e) {
            LOGGER.error("Failed to save badges", e);
        }
    }

    private static boolean badgesExist(String owner) {
        String SQL = "SELECT COUNT(*) FROM " + BADGES_TABLE_NAME + " WHERE owner = ?";

        try (PreparedStatement checkStatement = con.prepareStatement(SQL)) {
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
        String SQL = "SELECT badges FROM " + BADGES_TABLE_NAME + " WHERE owner = ?";
        ArrayList<Badge> badges = new ArrayList<>();

        try (PreparedStatement preparedStatement = con.prepareStatement(SQL);) {
            preparedStatement.setString(1, owner);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String badgesString = resultSet.getString("badges").toUpperCase();
                    if(!badgesString.isBlank()) {
                        String[] badgeStrings = badgesString.split(",");
                        for (String badge : badgeStrings) {
                            badges.add(Badge.valueOf(badge));
                        }
                        LOGGER.info("Returning {} {} for {}", badgeStrings.length, badgeStrings.length > 1 ? "badges" : "badge", owner);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to get badges", e);
        }
        return badges;
    }

    private static void clearTempData(String owner) {
        final String SQL_DELETE = "DELETE FROM " + RIOTAPI_DATA_TABLE_NAME + " WHERE json_extract(json_data, '$.owner') = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(SQL_DELETE)) {
            preparedStatement.setString(1, owner.toLowerCase());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Failed to clear temp data", e);
        }
    }
}