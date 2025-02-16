package net.maxxqc.mydrops.databases.types;

import net.maxxqc.mydrops.databases.DropPlayer;
import net.maxxqc.mydrops.databases.IDatabase;
import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.ProtectionType;
import net.maxxqc.mydrops.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;

public class SQLite extends IDatabase {
    private Connection connection;

    public Connection getSQLConnection() {
        File dataFolder = new File(Utils.plugin.getDataFolder(), ConfigManager.getDatabaseName() + ".db");

        if (!dataFolder.exists()) {
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                Utils.plugin.getLogger().log(Level.SEVERE, "File write error: " + ConfigManager.getDatabaseName() + ".db", e);
            }
        }

        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);

            return connection;
        } catch (SQLException | ClassNotFoundException ex) {
            Utils.plugin.getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
        }

        return null;
    }

    @Override
    public void load() {
        try (Connection conn = getSQLConnection()) {
            if (conn != null) {
                createTable(conn, ConfigManager.getDatabaseTablesPrefix() + "data", "`uuid` varchar(32) NOT NULL, `glow-color` varchar(20) NULL, PRIMARY KEY (`uuid`)");
                createTable(conn, ConfigManager.getDatabaseTablesPrefix() + "trust", "`uuid` varchar(32) NOT NULL, `trusted-uuid` varchar(32) NOT NULL, PRIMARY KEY (`uuid`)");
                createTable(conn, ConfigManager.getDatabaseTablesPrefix() + "trust_parties", "`uuid` varchar(32) NOT NULL, `trusted-uuid` varchar(32) NOT NULL, PRIMARY KEY (`uuid`)");
                createTable(conn, ConfigManager.getDatabaseTablesPrefix() + "protections", "`uuid` varchar(32) NOT NULL, `protection-type` varchar(255) NOT NULL, `value` boolean NOT NULL, PRIMARY KEY (`uuid`, `protection-type`)");
            }
        } catch (SQLException e) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Error while loading database", e);
        }

        connection = getSQLConnection();

        try (Connection conn = getSQLConnection(); PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + ConfigManager.getDatabaseTablesPrefix() + "data WHERE `uuid` = ?"); ResultSet rs = ps.executeQuery()) {
        } catch (SQLException ex) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Unable to retrieve connection", ex);
        }
    }

    private void createTable(Connection conn, String tableName, String tableDefinition) {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + tableDefinition + ");";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Error creating table: " + tableName, e);
        }
    }

    private DropPlayer loadDataFor(UUID src) {
        String glowColor = null;
        List<String> trustedPlayers = new ArrayList<>();
        List<String> trustedParties = new ArrayList<>();
        Map<ProtectionType, Boolean> protections = new HashMap<>();

        glowColor = executeQuery("SELECT * FROM " + ConfigManager.getDatabaseTablesPrefix() + "data WHERE `uuid` = ?;", src, rs -> rs.getString("glow-color"));
        trustedPlayers = executeQueryList("SELECT * FROM " + ConfigManager.getDatabaseTablesPrefix() + "trust WHERE `uuid` = ?;", src, rs -> rs.getString("trusted-uuid"));
        trustedParties = executeQueryList("SELECT * FROM " + ConfigManager.getDatabaseTablesPrefix() + "trust_parties WHERE `uuid` = ?;", src, rs -> rs.getString("trusted-uuid"));
        protections = executeQueryMap("SELECT * FROM " + ConfigManager.getDatabaseTablesPrefix() + "protections WHERE `uuid` = ?;", src, rs -> ProtectionType.fromValue(rs.getString("protection-type")), rs -> rs.getBoolean("value"));

        DropPlayer dropPlayer = new DropPlayer(src, glowColor, trustedPlayers, trustedParties, protections);
        CACHE.put(src, dropPlayer);

        return dropPlayer;
    }

    private <T> T executeQuery(String query, UUID src, ResultSetExtractor<T> extractor) {
        try (Connection conn = getSQLConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, src.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractor.extractData(rs);
                }
            }
        } catch (SQLException ex) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQLite statement: ", ex);
        }
        return null;
    }

    private <T> List<T> executeQueryList(String query, UUID src, ResultSetExtractor<T> extractor) {
        List<T> result = new ArrayList<>();
        try (Connection conn = getSQLConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, src.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(extractor.extractData(rs));
                }
            }
        } catch (SQLException ex) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQLite statement: ", ex);
        }
        return result;
    }

    private <K, V> Map<K, V> executeQueryMap(String query, UUID src, ResultSetExtractor<K> keyExtractor, ResultSetExtractor<V> valueExtractor) {
        Map<K, V> result = new HashMap<>();
        try (Connection conn = getSQLConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, src.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(keyExtractor.extractData(rs), valueExtractor.extractData(rs));
                }
            }
        } catch (SQLException ex) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQLite statement: ", ex);
        }
        return result;
    }

    @Override
    public String getGlowColor(UUID player) {
        return getCachedOrLoad(player, DropPlayer::getGlowColor, ConfigManager.getGlowColor().name());
    }

    @Override
    public void setGlowColor(UUID player, String color) {
        if (color == null || color.equalsIgnoreCase(ConfigManager.getGlowColor().name())) {
            removePlayerData(player);
        } else {
            insertOrReplacePlayerData(player, color);
        }
    }

    private void removePlayerData(UUID player) {
        executeUpdate("DELETE FROM " + ConfigManager.getDatabaseTablesPrefix() + "data WHERE `uuid` = ?;", player);
        updateCache(player, dp -> dp.setGlowColor(null));
    }

    private void insertOrReplacePlayerData(UUID player, String color) {
        executeUpdate("INSERT OR REPLACE INTO " + ConfigManager.getDatabaseTablesPrefix() + "data (`uuid`, `glow-color`) VALUES(?, ?);", player, color);
        updateCache(player, dp -> dp.setGlowColor(color));
    }

    @Override
    public void addTrustedPlayer(UUID src, String target) {
        executeUpdate("INSERT OR REPLACE INTO " + ConfigManager.getDatabaseTablesPrefix() + "trust (`uuid`, `trusted-uuid`) VALUES(?, ?);", src, target);
        updateCache(src, dp -> dp.addTrustedPlayer(target));
    }

    @Override
    public void removeTrustedPlayer(UUID src, String target) {
        executeUpdate("DELETE FROM " + ConfigManager.getDatabaseTablesPrefix() + "trust WHERE `uuid` = ? AND `trusted-uuid` = ?;", src, target);
        updateCache(src, dp -> dp.removeTrustedPlayer(target));
    }

    @Override
    public void addTrustedParty(UUID src, String partyId) {
        executeUpdate("INSERT OR REPLACE INTO " + ConfigManager.getDatabaseTablesPrefix() + "trust_parties (`uuid`, `trusted-uuid`) VALUES(?, ?);", src, partyId);
        updateCache(src, dp -> dp.addTrustedParty(partyId));
    }

    @Override
    public void removeTrustedParty(UUID src, String partyId) {
        executeUpdate("DELETE FROM " + ConfigManager.getDatabaseTablesPrefix() + "trust_parties WHERE `uuid` = ? AND `trusted-uuid` = ?;", src, partyId);
        updateCache(src, dp -> dp.removeTrustedParty(partyId));
    }

    @Override
    public List<String> getTrustedPlayers(UUID player) {
        return getCachedOrLoad(player, DropPlayer::getTrustedPlayers, Collections.emptyList());
    }

    @Override
    public List<String> getTrustedParties(UUID src) {
        return getCachedOrLoad(src, DropPlayer::getTrustedParties, Collections.emptyList());
    }

    @Override
    public void setProtection(UUID src, ProtectionType protectionType, boolean value) {
        if (ConfigManager.hasServerProtection(protectionType) == value) {
            executeUpdate("DELETE FROM " + ConfigManager.getDatabaseTablesPrefix() + "protections WHERE `uuid` = ? AND `protection-type` = ?;", src, protectionType.getStringValue());
            updateCache(src, dp -> dp.removeProtection(protectionType));
        } else {
            executeUpdate("INSERT OR REPLACE INTO " + ConfigManager.getDatabaseTablesPrefix() + "protections (`uuid`, `protection-type`, `value`) VALUES(?, ?, ?);", src, protectionType.getStringValue(), value);
            updateCache(src, dp -> dp.setProtection(protectionType, value));
        }
    }

    @Override
    public boolean getProtection(UUID src, ProtectionType protectionType) {
        return getCachedOrLoad(src, dp -> dp.getProtection(protectionType), ConfigManager.hasServerProtection(protectionType));
    }

    private void executeUpdate(String query, UUID src, Object... params) {
        try (Connection conn = getSQLConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, src.toString());
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 2, params[i]);
            }
            ps.executeUpdate();
        } catch (SQLException ex) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQLite statement: ", ex);
        }
    }

    private <T> T getCachedOrLoad(UUID src, Function<DropPlayer, T> getter, T defaultValue) {
        if (CACHE.containsKey(src)) {
            return getter.apply(CACHE.get(src));
        }

        DropPlayer dropPlayer = loadDataFor(src);
        return dropPlayer == null ? defaultValue : getter.apply(dropPlayer);
    }

    private void updateCache(UUID src, Consumer<DropPlayer> updater) {
        if (CACHE.containsKey(src)) {
            updater.accept(CACHE.get(src));
        } else {
            loadDataFor(src);
        }
    }

    @FunctionalInterface
    private interface ResultSetExtractor<T> {
        T extractData(ResultSet rs) throws SQLException;
    }
}