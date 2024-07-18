package net.maxxqc.mydrops.databases.file;

import net.maxxqc.mydrops.databases.IDatabase;
import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public class FileDatabase extends IDatabase {
    private static FileConfiguration config;
    private static File configFile;

    @Override
    public String getGlowColor(Player player) {
        String color = config.getString(player.getUniqueId() + ".glow-color");

        if (color == null)
            return ConfigManager.getGlowColor().name();

        return color;
    }

    @Override
    public void setGlowColor(Player player, ChatColor color) {
        setGlowColor(player, color == null ? null : color.name());
    }

    @Override
    public void setGlowColor(Player player, String color) {
        config.set(player.getUniqueId() + ".glow-color", color);
        saveFile();
    }

    @Override
    public void addTrustedPlayer(Player src, Player target) {
        List<String> trustedPlayers = config.getStringList(src.getUniqueId() + ".trusted-players");

        if (trustedPlayers.contains(target.getUniqueId().toString()))
            return;

        trustedPlayers.add(target.getUniqueId().toString());
        config.set(src.getUniqueId() + ".trusted-players", trustedPlayers);
        saveFile();
    }

    @Override
    public void removeTrustedPlayer(Player src, Player target) {
        List<String> trustedPlayers = config.getStringList(src.getUniqueId() + ".trusted-players");

        if (!trustedPlayers.contains(target.getUniqueId().toString()))
            return;

        trustedPlayers.remove(target.getUniqueId().toString());
        config.set(src.getUniqueId() + ".trusted-players", trustedPlayers);
        saveFile();
    }

    @Override
    public List<String> getTrustedPlayers(Player player) {
        return config.getStringList(player.getUniqueId() + ".trusted-players");
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
