package net.maxxqc.mydrops.utils;

public class Constants
{
    public static final int BSTATS_PLUGIN_ID = 19913;
    public static final int SPIGOT_RESOURCE_ID = 112721;

    public static String UPDATER_NEW_VERSION = "";
    public static String CURRENT_VERSION = "";

    public static void markUpdateAvailable(String newVersion)
    {
        UPDATER_NEW_VERSION = newVersion;
    }

    public static boolean updateAvailable()
    {
        return !UPDATER_NEW_VERSION.equals("");
    }

    public static void setCurrentVersion(String version)
    {
        CURRENT_VERSION = version;
    }
}