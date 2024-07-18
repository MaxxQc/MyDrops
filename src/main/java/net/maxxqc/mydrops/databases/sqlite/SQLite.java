package net.maxxqc.mydrops.databases.sqlite;

import net.maxxqc.mydrops.databases.DropPlayer;
import net.maxxqc.mydrops.databases.IDatabase;
import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

// Adapted from https://www.spigotmc.org/threads/how-to-sqlite.56847/
public class SQLite extends IDatabase {
    private Connection connection;

    public Connection getSQLConnection() {
        File dataFolder = new File(Utils.plugin.getDataFolder(), ConfigManager.getDatabaseName() + ".db");

        if (!dataFolder.exists()) {
            try {
                dataFolder.createNewFile();
            }
            catch (IOException e) {
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
        }
        catch (SQLException ex) {
            Utils.plugin.getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
        }
        catch (ClassNotFoundException ex) {
            Utils.plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library.");
        }

        return null;
    }

    public void load() {
        try (Connection conn = getSQLConnection()) {
            if (conn != null) {
                createTable(conn, ConfigManager.getDatabaseTablesPrefix() + "data", "`uuid` varchar(32) NOT NULL, `glow-color` varchar(20) NULL, PRIMARY KEY (`uuid`)");

                createTable(conn, ConfigManager.getDatabaseTablesPrefix() + "trust", "`uuid` varchar(32) NOT NULL, `trusted-uuid` varchar(32) NOT NULL, PRIMARY KEY (`uuid`)");
            }
        }
        catch (SQLException e) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Error while loading database", e);
        }

        connection = getSQLConnection();

        try (Connection conn = getSQLConnection(); PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + ConfigManager.getDatabaseTablesPrefix() + "data WHERE `uuid` = ?"); ResultSet rs = ps.executeQuery()) {
        }
        catch (SQLException ex) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Unable to retrieve connection", ex);
        }
    }

    private void createTable(Connection conn, String tableName, String tableDefinition) {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + tableDefinition + ");";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
        catch (SQLException e) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Error creating table: " + tableName, e);
        }
    }

    private DropPlayer loadDataFor(Player player) {
        String glowColor = null;
        List<String> trustedPlayers = new ArrayList<>();

        String glowQuery = "SELECT * FROM " + ConfigManager.getDatabaseTablesPrefix() + "data WHERE `uuid` = ?;";
        try (Connection conn = getSQLConnection(); PreparedStatement ps = conn.prepareStatement(glowQuery)) {
            ps.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    glowColor = rs.getString("glow-color");
                }
            }
        }
        catch (SQLException ex) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQLite statement: ", ex);
            return null;
        }

        String trustedPlayersQuery = "SELECT * FROM " + ConfigManager.getDatabaseTablesPrefix() + "trust WHERE `uuid` = ?;";
        try (Connection conn = getSQLConnection(); PreparedStatement ps = conn.prepareStatement(trustedPlayersQuery)) {
            ps.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    trustedPlayers.add(rs.getString("trusted-uuid"));
                }
            }
        }
        catch (SQLException ex) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQLite statement: ", ex);
            return null;
        }

        DropPlayer dropPlayer = new DropPlayer(player.getUniqueId(), glowColor, trustedPlayers);
        CACHE.put(player.getUniqueId(), dropPlayer);

        return dropPlayer;
    }

    public String getGlowColor(Player player) {
        if (CACHE.containsKey(player.getUniqueId())) {
            return CACHE.get(player.getUniqueId()).getGlowColor();
        }

        DropPlayer dropPlayer = loadDataFor(player);
        return dropPlayer == null ? ConfigManager.getGlowColor().name() : dropPlayer.getGlowColor();
    }

    public void setGlowColor(Player player, ChatColor color) {
        setGlowColor(player, color == null ? null : color.name());
    }

    public void setGlowColor(Player player, String color) {
        if (color == null || color.equalsIgnoreCase(ConfigManager.getGlowColor().name())) {
            removePlayerData(player);
        }
        else {
            insertOrReplacePlayerData(player, color);
        }
    }

    private void removePlayerData(Player player) {
        String query = "DELETE FROM " + ConfigManager.getDatabaseTablesPrefix() + "data WHERE `uuid` = ?;";

        try (Connection conn = getSQLConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, player.getUniqueId().toString());
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQLite statement: ", ex);
        }

        if (CACHE.containsKey(player.getUniqueId())) {
            CACHE.get(player.getUniqueId()).setGlowColor(null);
        }
    }

    private void insertOrReplacePlayerData(Player player, String color) {
        String query = "INSERT OR REPLACE INTO " + ConfigManager.getDatabaseTablesPrefix() + "data (`uuid`, `glow-color`) VALUES(?, ?);";

        try (Connection conn = getSQLConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, color);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQLite statement: ", ex);
        }

        if (CACHE.containsKey(player.getUniqueId())) {
            CACHE.get(player.getUniqueId()).setGlowColor(color);
        }
        else {
            loadDataFor(player);
        }
    }

    public void addTrustedPlayer(Player src, Player target) {
        String query = "INSERT OR REPLACE INTO " + ConfigManager.getDatabaseTablesPrefix() + "trust (`uuid`, `trusted-uuid`) VALUES(?, ?);";

        try (Connection conn = getSQLConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, src.getUniqueId().toString());
            ps.setString(2, target.getUniqueId().toString());
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQLite statement: ", ex);
        }

        if (CACHE.containsKey(src.getUniqueId())) {
            CACHE.get(src.getUniqueId()).addTrustedPlayer(target.getUniqueId().toString());
        }
        else {
            loadDataFor(src);
        }
    }

    public void removeTrustedPlayer(Player src, Player target) {
        String query = "DELETE FROM " + ConfigManager.getDatabaseTablesPrefix() + "trust WHERE `uuid` = ? AND `trusted-uuid` = ?;";

        try (Connection conn = getSQLConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, src.getUniqueId().toString());
            ps.setString(2, target.getUniqueId().toString());
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQLite statement: ", ex);
        }

        if (CACHE.containsKey(src.getUniqueId())) {
            CACHE.get(src.getUniqueId()).removeTrustedPlayer(target.getUniqueId().toString());
        }
    }

    public List<String> getTrustedPlayers(Player player) {
        if (CACHE.containsKey(player.getUniqueId())) {
            return CACHE.get(player.getUniqueId()).getTrustedPlayers();
        }

        DropPlayer dropPlayer = loadDataFor(player);
        return dropPlayer == null ? Collections.emptyList() : dropPlayer.getTrustedPlayers();
    }
}
