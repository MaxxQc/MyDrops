package net.maxxqc.mydrops.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager
{
    private static FileConfiguration config;

    private static DatabaseType databaseType;

    private static String msgCmdPlayerOnly;

    public static void init(JavaPlugin plugin) {
        config = plugin.getConfig();

        config.addDefault("options.glow", true);
        config.addDefault("options.default-glow-color", "AQUA");
        config.addDefault("options.per-player-glow", true);
        config.addDefault("options.per-player-protection", false);
        config.addDefault("options.database-format", "file");
        config.addDefault("options.enable-bstats", true);

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

        config.addDefault("messages.commands.player-only", "&cYou must be a player to use this command.");
        config.addDefault("messages.commands.unknown", "&cUnknown subcommand &4&o{subcommand}");
        config.addDefault("messages.commands.no-permission", "&cYou do not have permission to execute subcommand &4&o{subcommand}");
        config.addDefault("messages.commands.glow.set", "&eGlow color set to &o{color}");
        config.addDefault("messages.commands.glow.invalid", "&eGlow color &o&6{color}&e is not valid");

        config.options().copyDefaults(true);
        plugin.saveConfig();
    }

    public static DatabaseType getDatabaseType()
    {
        if (databaseType == null)
            databaseType = DatabaseType.valueOf(config.getString("options.database-format", "file").toUpperCase());

        return databaseType;
    }

    public static boolean hasOptionGlow() {
        return config.getBoolean("options.glow", true);
    }

    private static ChatColor glowColor;

    public static ChatColor getGlowColor() {
        if (glowColor == null)
        {
            try
            {
                glowColor = ChatColor.valueOf(config.getString("options.default-glow-color", "AQUA").toUpperCase());
            }
            catch (Exception e)
            {
                glowColor = ChatColor.AQUA;
                e.printStackTrace();
            }
        }

        return glowColor;
    }

    public static boolean hasPerPlayerGlow()
    {
        return config.getBoolean("options.glow", true) && config.getBoolean("options.per-player-glow", true);
    }

    public static boolean hasItemDropProtection() {
        return config.getBoolean("protection.item-drop.enable", true);
    }

    public static boolean hasBlockBreakProtection() {
        return config.getBoolean("protection.block-break.enable", true);
    }

    public static boolean hasVehicleDestroyProtection() {
        return config.getBoolean("protection.vehicle-destroy.enable", true);
    }

    public static boolean hasHangingBreakProtection() {
        return config.getBoolean("protection.hanging-break.enable", true);
    }

    public static boolean hasItemFrameDropProtection() {
        return config.getBoolean("protection.item-frame-drop.enable", true);
    }

    public static boolean hasEntityKillProtection() {
        return config.getBoolean("protection.entity-kill.enable", true);
    }

    public static boolean hasPlayerDeathProtection() {
        return config.getBoolean("protection.player-death.enable", false);
    }

    public static boolean hasBStats()
    {
        return config.getBoolean("options.enable-bstats", true);
    }

    public static String getMsgCmdPlayerOnly()
    {
        if (msgCmdPlayerOnly == null)
            msgCmdPlayerOnly = Utils.colorize(config.getString("messages.commands.player-only", "You must be a player to use this command."));

        return msgCmdPlayerOnly;
    }

    public static String getMsgCmdUnknownSub()
    {
        return config.getString("messages.commands.unknown", "&cUnknown subcommand &4&o{subcommand}");
    }

    public static String getMsgCmdNoPermission()
    {
        return config.getString("messages.commands.no-permission", "&cYou do not have permission to execute subcommand &4&o{subcommand}");
    }

    public static String getMsgCmdGlowSet()
    {
        return config.getString("messages.commands.glow.set", "&eGlow color set to &o{color}");
    }

    public static String getMsgCmdGlowInvalid()
    {
        return config.getString("messages.commands.glow.invalid", "&eGlow color &o&6{color}&e is not valid");
    }
}