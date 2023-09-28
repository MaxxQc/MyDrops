package net.maxxqc.mydrops.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager
{
    private static FileConfiguration config;

    public static void init(JavaPlugin plugin) {
        config = plugin.getConfig();

        config.addDefault("options.glow", true);
        config.addDefault("options.default-glow-color", "AQUA");
        config.addDefault("options.per-player-glow", true);
        config.addDefault("options.per-player-protection", false);
        config.addDefault("options.database-format", "file");

        config.addDefault("protection.item-drop.enable", true);
        config.addDefault("protection.item-drop.player-default", true);
        config.addDefault("protection.block-break.enable", true);
        config.addDefault("protection.block-break.player-default", true);
        config.addDefault("protection.item-frame-drop.enable", true);
        config.addDefault("protection.item-frame-drop.player-default", true);
        config.addDefault("protection.vehicle-destroy.enable", true);
        config.addDefault("protection.vehicle-destroy.player-default", true);
        config.addDefault("protection.hanging-break.enable", true);
        config.addDefault("protection.hanging-break.player-default", true);
        config.addDefault("protection.entity-kill.enable", true);
        config.addDefault("protection.entity-kill.player-default", true);
        config.addDefault("protection.player-death.enable", false);
        config.addDefault("protection.player-death.player-default", false);

        config.options().copyDefaults(true);
        plugin.saveConfig();
    }

    public static boolean hasOptionGlow() {
        return config.getBoolean("options.glow");
    }

    private static ChatColor glowColor;

    public static ChatColor getGlowColor() {
        if (glowColor == null)
        {
            try
            {
                glowColor = ChatColor.valueOf(config.getString("options.default-glow-color").toUpperCase());
            }
            catch (Exception e)
            {
                glowColor = ChatColor.AQUA;
                e.printStackTrace();
            }
        }

        return glowColor;
    }

    public static boolean hasItemDropProtection() {
        return config.getBoolean("protection.item-drop.enable");
    }

    public static boolean hasBlockBreakProtection() {
        return config.getBoolean("protection.block-break.enable");
    }

    public static boolean hasVehicleDestroyProtection() {
        return config.getBoolean("protection.vehicle-destroy.enable");
    }

    public static boolean hasHangingBreakProtection() {
        return config.getBoolean("protection.hanging-break.enable");
    }

    public static boolean hasItemFrameDropProtection() {
        return config.getBoolean("protection.item-frame-drop.enable");
    }

    public static boolean hasEntityKillProtection() {
        return config.getBoolean("protection.entity-kill.enable");
    }

    public static boolean hasPlayerDeathProtection() {
        return config.getBoolean("protection.player-death.enable");
    }
}