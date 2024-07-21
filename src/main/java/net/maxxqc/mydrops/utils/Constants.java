package net.maxxqc.mydrops.utils;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Constants {
    public static final int BSTATS_PLUGIN_ID = 19913;
    public static final int SPIGOT_RESOURCE_ID = 112721;

    public static String UPDATER_NEW_VERSION = "";
    public static String CURRENT_VERSION = "";

    public static void markUpdateAvailable(String newVersion) {
        UPDATER_NEW_VERSION = newVersion;
    }

    public static boolean updateAvailable() {
        return !UPDATER_NEW_VERSION.equals("");
    }

    public static void setCurrentVersion(String version) {
        CURRENT_VERSION = version;
    }

    public static final Map<String, Material> ALL_COLORS = Map.ofEntries(Map.entry("AQUA", Material.LIGHT_BLUE_WOOL), Map.entry("BLACK", Material.BLACK_WOOL), Map.entry("BLUE", Material.BLUE_WOOL), Map.entry("DARK_AQUA", Material.CYAN_WOOL), Map.entry("DARK_BLUE", Material.BLUE_WOOL), Map.entry("DARK_GRAY", Material.GRAY_WOOL), Map.entry("DARK_GREEN", Material.GREEN_WOOL), Map.entry("DARK_PURPLE", Material.PURPLE_WOOL), Map.entry("DARK_RED", Material.RED_WOOL), Map.entry("GOLD", Material.ORANGE_WOOL), Map.entry("GRAY", Material.LIGHT_GRAY_WOOL), Map.entry("GREEN", Material.LIME_WOOL), Map.entry("LIGHT_PURPLE", Material.PINK_WOOL), Map.entry("RED", Material.RED_WOOL), Map.entry("WHITE", Material.WHITE_WOOL), Map.entry("YELLOW", Material.YELLOW_WOOL));

    public static final Map<String, String> PLAYER_CONFIG_CHAT_MAP = new HashMap<>();

    public static final List<String> DATABASE_FORMATS = List.of("sqlite", "file", "mysql", "mongodb");
}