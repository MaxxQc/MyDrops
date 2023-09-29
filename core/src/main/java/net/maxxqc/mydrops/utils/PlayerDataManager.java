package net.maxxqc.mydrops.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

public class PlayerDataManager
{
    private static FileConfiguration config;
    private static File configFile;

    public static void init(JavaPlugin plugin)
    {
        if (ConfigManager.getDatabaseType() == DatabaseType.FILE)
        {
            configFile = new File(plugin.getDataFolder(), "players.yml");
            plugin.saveResource("players.yml", false);
            config = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("players.yml")));
            saveFile();
        }
        else
        {
            plugin.getLogger().warning("Cannot initialize player database");
            plugin.getLogger().warning("Per player configurations are now disabled");
            plugin.getLogger().warning("Make sure that you selected a valid database format");
        }
    }

    private static void saveFile()
    {
        try
        {
            config.save(configFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void saveGlowColor(UUID uniqueId, String s)
    {
        config.set(uniqueId + ".glow-color", s);
        saveFile();
    }

    public static ChatColor getGlowColor(Player player)
    {
        String color = config.getString(player.getUniqueId() + ".glow-color");
        if (color == null)
            return ConfigManager.getGlowColor();

        return color.equalsIgnoreCase("none") ? null : ChatColor.valueOf(color);
    }
}