package net.maxxqc.mydrops.databases.types;

import net.maxxqc.mydrops.databases.DropPlayer;
import net.maxxqc.mydrops.databases.IDatabase;
import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.ChatColor;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import static net.maxxqc.mydrops.utils.ConfigManager.*;

// Adapted from https://www.spigotmc.org/threads/how-to-sqlite.56847/
public class MySQL extends IDatabase {
    private Connection connection;

    public Connection getSQLConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + getDatabaseHost() + ":" + getDatabasePort() + "/" + getDatabaseName(), getDatabaseUser(), getDatabasePassword());

            return connection;
        }
        catch (SQLException ex) {
            Utils.plugin.getLogger().log(Level.SEVERE, "MySQL exception on initialize", ex);
        }
        catch (ClassNotFoundException ex) {
            Utils.plugin.getLogger().log(Level.SEVERE, "You need the MySQL JBDC library.");
        }

        return null;
    }

    public void load() {
        try (Connection conn = getSQLConnection()) {
            if (conn != null) {
                createTable(conn, ConfigManager.getDatabaseTablesPrefix() + "data", "`uuid` varchar(36) NOT NULL, `glow-color` varchar(20) NULL, PRIMARY KEY (`uuid`)");

                createTable(conn, ConfigManager.getDatabaseTablesPrefix() + "trust", "`uuid` varchar(36) NOT NULL, `trusted-uuid` varchar(36) NOT NULL, PRIMARY KEY (`uuid`)");

                createTable(conn, ConfigManager.getDatabaseTablesPrefix() + "trust_parties", "`uuid` varchar(36) NOT NULL, `trusted-uuid` varchar(36) NOT NULL, PRIMARY KEY (`uuid`)");
            }
        }
        catch (SQLException e) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Error while loading database", e);
        }

        connection = getSQLConnection();

        try (Connection conn = getSQLConnection(); PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + ConfigManager.getDatabaseTablesPrefix() + "data LIMIT 1"); ResultSet rs = ps.executeQuery()) {
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

    private DropPlayer loadDataFor(UUID player) {
        String glowColor = null;
        List<String> trustedPlayers = new ArrayList<>();
        List<String> trustedParties = new ArrayList<>();

        String glowQuery = "SELECT * FROM " + ConfigManager.getDatabaseTablesPrefix() + "data WHERE `uuid` = ?;";
        try (Connection conn = getSQLConnection(); PreparedStatement ps = conn.prepareStatement(glowQuery)) {
            ps.setString(1, player.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    glowColor = rs.getString("glow-color");
                }
            }
        }
        catch (SQLException ex) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
            return null;
        }

        String trustedPlayersQuery = "SELECT * FROM " + ConfigManager.getDatabaseTablesPrefix() + "trust WHERE `uuid` = ?;";
        try (Connection conn = getSQLConnection(); PreparedStatement ps = conn.prepareStatement(trustedPlayersQuery)) {
            ps.setString(1, player.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    trustedPlayers.add(rs.getString("trusted-uuid"));
                }
            }
        }
        catch (SQLException ex) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
            return null;
        }

        String trustedPartiesQuery = "SELECT * FROM " + ConfigManager.getDatabaseTablesPrefix() + "trust_parties WHERE `uuid` = ?;";
        try (Connection conn = getSQLConnection(); PreparedStatement ps = conn.prepareStatement(trustedPartiesQuery)) {
            ps.setString(1, player.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    trustedParties.add(rs.getString("trusted-uuid"));
                }
            }
        }
        catch (SQLException ex) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
            return null;
        }

        DropPlayer dropPlayer = new DropPlayer(player, glowColor, trustedPlayers, trustedParties);
        CACHE.put(player, dropPlayer);

        return dropPlayer;
    }

    public String getGlowColor(UUID player) {
        if (CACHE.containsKey(player)) {
            return CACHE.get(player).getGlowColor();
        }

        DropPlayer dropPlayer = loadDataFor(player);
        return dropPlayer == null ? ConfigManager.getGlowColor().name() : dropPlayer.getGlowColor();
    }

    public void setGlowColor(UUID player, String color) {
        if (color == null || color.equalsIgnoreCase(ConfigManager.getGlowColor().name())) {
            removePlayerData(player);
        }
        else {
            insertOrReplacePlayerData(player, color);
        }
    }

    private void removePlayerData(UUID player) {
        String query = "DELETE FROM " + ConfigManager.getDatabaseTablesPrefix() + "data WHERE `uuid` = ?;";

        try (Connection conn = getSQLConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, player.toString());
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
        }

        if (CACHE.containsKey(player)) {
            CACHE.get(player).setGlowColor(null);
        }
    }

    private void insertOrReplacePlayerData(UUID player, String color) {
        String query = "REPLACE INTO " + ConfigManager.getDatabaseTablesPrefix() + "data (`uuid`, `glow-color`) VALUES(?, ?);";

        try (Connection conn = getSQLConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, player.toString());
            ps.setString(2, color);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
        }

        if (CACHE.containsKey(player)) {
            CACHE.get(player).setGlowColor(color);
        }
        else {
            loadDataFor(player);
        }
    }

    public void addTrustedPlayer(UUID src, String target) {
        String query = "REPLACE INTO " + ConfigManager.getDatabaseTablesPrefix() + "trust (`uuid`, `trusted-uuid`) VALUES(?, ?);";

        try (Connection conn = getSQLConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, src.toString());
            ps.setString(2, target);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
        }

        if (CACHE.containsKey(src)) {
            CACHE.get(src).addTrustedPlayer(target);
        }
        else {
            loadDataFor(src);
        }
    }

    public void removeTrustedPlayer(UUID src, String target) {
        String query = "DELETE FROM " + ConfigManager.getDatabaseTablesPrefix() + "trust WHERE `uuid` = ? AND `trusted-uuid` = ?;";

        try (Connection conn = getSQLConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, src.toString());
            ps.setString(2, target.toString());
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
        }

        if (CACHE.containsKey(src)) {
            CACHE.get(src).removeTrustedPlayer(target.toString());
        }
    }

    public void addTrustedParty(UUID src, String partyId) {
        String query = "REPLACE INTO " + ConfigManager.getDatabaseTablesPrefix() + "trust_parties (`uuid`, `trusted-uuid`) VALUES(?, ?);";

        try (Connection conn = getSQLConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, src.toString());
            ps.setString(2, partyId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
        }

        if (CACHE.containsKey(src)) {
            CACHE.get(src).addTrustedParty(partyId);
        }
        else {
            loadDataFor(src);
        }
    }

    public void removeTrustedParty(UUID src, String partyId) {
        String query = "DELETE FROM " + ConfigManager.getDatabaseTablesPrefix() + "trust_parties WHERE `uuid` = ? AND `trusted-uuid` = ?;";

        try (Connection conn = getSQLConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, src.toString());
            ps.setString(2, partyId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
        }

        if (CACHE.containsKey(src)) {
            CACHE.get(src).removeTrustedParty(partyId);
        }
    }

    public List<String> getTrustedPlayers(UUID player) {
        if (CACHE.containsKey(player)) {
            return CACHE.get(player).getTrustedPlayers();
        }

        DropPlayer dropPlayer = loadDataFor(player);
        return dropPlayer == null ? Collections.emptyList() : dropPlayer.getTrustedPlayers();
    }

    public List<String> getTrustedParties(UUID player) {
        if (CACHE.containsKey(player)) {
            return CACHE.get(player).getTrustedParties();
        }

        DropPlayer dropPlayer = loadDataFor(player);
        return dropPlayer == null ? Collections.emptyList() : dropPlayer.getTrustedParties();
    }
}
