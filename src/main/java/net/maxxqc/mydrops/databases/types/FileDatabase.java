package net.maxxqc.mydrops.databases.types;

import net.maxxqc.mydrops.databases.IDatabase;
import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class FileDatabase extends IDatabase {
    private static FileConfiguration config;
    private static File configFile;

    @Override
    public String getGlowColor(UUID player) {
        String color = config.getString(player + ".glow-color");

        if (color == null)
            return ConfigManager.getGlowColor().name();

        return color;
    }

    @Override
    public void setGlowColor(UUID player, String color) {
        config.set(player + ".glow-color", color);
        saveFile();
    }

    @Override
    public void addTrustedPlayer(UUID src, String target) {
        List<String> trustedPlayers = config.getStringList(src + ".trusted-players");

        if (trustedPlayers.contains(target))
            return;

        trustedPlayers.add(target);
        config.set(src + ".trusted-players", trustedPlayers);
        saveFile();
    }

    @Override
    public void removeTrustedPlayer(UUID src, String target) {
        List<String> trustedPlayers = config.getStringList(src + ".trusted-players");

        if (!trustedPlayers.contains(target))
            return;

        trustedPlayers.remove(target);
        config.set(src + ".trusted-players", trustedPlayers);
        saveFile();
    }

    @Override
    public void addTrustedParty(UUID src, String partyId) {
        List<String> trustedParties = config.getStringList(src + ".trusted-parties");

        if (trustedParties.contains(partyId))
            return;

        trustedParties.add(partyId);
        config.set(src + ".trusted-parties", trustedParties);
        saveFile();
    }

    @Override
    public void removeTrustedParty(UUID src, String partyId) {
        List<String> trustedParties = config.getStringList(src + ".trusted-parties");

        if (!trustedParties.contains(partyId))
            return;

        trustedParties.remove(partyId);
        config.set(src + ".trusted-parties", trustedParties);
        saveFile();
    }

    @Override
    public List<String> getTrustedPlayers(UUID player) {
        return config.getStringList(player + ".trusted-players");
    }

    @Override
    public List<String> getTrustedParties(UUID player) {
        return config.getStringList(player + ".trusted-parties");
    }

    @Override
    public void load() {
        configFile = new File(Utils.plugin.getDataFolder(), "players.yml");

        if (!configFile.exists())
            Utils.plugin.saveResource("players.yml", false);

        config = YamlConfiguration.loadConfiguration(configFile);
        saveFile();
    }

    private void saveFile()
    {
        try {
            config.save(configFile);
        }
        catch (IOException e) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Error saving player file: ", e);
        }
    }
}
