package net.maxxqc.mydrops.databases.types;

import net.maxxqc.mydrops.databases.IDatabase;
import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.ProtectionType;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class FileDatabase extends IDatabase {
    private static FileConfiguration saveConfig;
    private static File configFile;

    @Override
    public String getGlowColor(UUID player) {
        return saveConfig.getString(player + ".glow-color", ConfigManager.getGlowColor().name());
    }

    @Override
    public void setGlowColor(UUID player, String color) {
        saveConfig.set(player + ".glow-color", color);
        saveFile();
    }

    @Override
    public void addTrustedPlayer(UUID src, String target) {
        updateList(src + ".trusted-players", target, true);
    }

    @Override
    public void removeTrustedPlayer(UUID src, String target) {
        updateList(src + ".trusted-players", target, false);
    }

    @Override
    public void addTrustedParty(UUID src, String partyId) {
        updateList(src + ".trusted-parties", partyId, true);
    }

    @Override
    public void removeTrustedParty(UUID src, String partyId) {
        updateList(src + ".trusted-parties", partyId, false);
    }

    @Override
    public List<String> getTrustedPlayers(UUID player) {
        return saveConfig.getStringList(player + ".trusted-players");
    }

    @Override
    public List<String> getTrustedParties(UUID player) {
        return saveConfig.getStringList(player + ".trusted-parties");
    }

    @Override
    public boolean getProtection(UUID src, ProtectionType protectionType) {
        String path = src + ".protection." + protectionType.getStringValue();
        return saveConfig.getBoolean(path, ConfigManager.hasServerProtection(protectionType));
    }

    @Override
    public void setProtection(UUID src, ProtectionType protectionType, boolean newValue) {
        String path = src + ".protection." + protectionType.getStringValue();
        boolean serverValue = ConfigManager.hasServerProtection(protectionType);
        saveConfig.set(path, newValue == serverValue ? null : newValue);
        saveFile();
    }

    @Override
    public void load() {
        configFile = new File(Utils.plugin.getDataFolder(), "players.yml");

        if (!configFile.exists()) {
            Utils.plugin.saveResource("players.yml", false);
        }

        saveConfig = YamlConfiguration.loadConfiguration(configFile);
        saveFile();
    }

    private void saveFile() {
        try {
            saveConfig.save(configFile);
        } catch (IOException e) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Error saving player file: ", e);
        }
    }

    private void updateList(String path, String value, boolean add) {
        List<String> list = saveConfig.getStringList(path);
        if (add) {
            if (!list.contains(value)) {
                list.add(value);
            }
        } else {
            list.remove(value);
        }
        saveConfig.set(path, list);
        saveFile();
    }
}