package net.maxxqc.mydrops.utils;

import net.maxxqc.mydrops.events.AutoUpdaterHandler;
import net.maxxqc.mydrops.protection.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

import static com.mojang.logging.LogUtils.getLogger;

public class ConfigManager
{
    private static JavaPlugin plugin;
    private static FileConfiguration config;

    private static DatabaseType databaseType;
    private static String msgCmdPlayerOnly;
    private static String msgCmdReloaded;
    private static String msgCmdInvalidItem;
    private static String msgCmdTrashTitle;
    private static String msgCmdConfigInvalidKey;
    private static String msgCmdConfigInvalidValue;
    private static String msgCmdConfigPosValue;
    private static String msgCmdConfigSuccess;
    private static String msgCmdConfigUsage;
    private static String msgCmdConfigAdded;
    private static String msgCmdConfigRemoved;
    private static String msgCmdConfigValue;
    private static String msgCmdConfigType;
    private static String msgCmdConfigPlaceholders;
    private static String msgCmdConfigCurrentValue;
    private static String msgCmdConfigInputCancelled;
    private static String msgCmdConfigRightClick;
    private static String msgCmdConfigLeftClick;
    private static String msgCmdConfigShiftClick;
    private static String txtConfirmTitle;
    private static String txtConfigGUITitle;
    private static String txtColorGUITitle;
    private static ChatColor glowColor;

    private static ItemStack defaultAcceptItem;
    private static ItemStack acceptItem;
    private static ItemStack defaultDeclineItem;
    private static ItemStack declineItem;
    private static ItemStack defaultBackItem;
    private static ItemStack backItem;
    private static ItemStack defaultCloseItem;
    private static ItemStack closeItem;

    public static Map<String, List<String>> CONFIGS_ARGS = new HashMap<>();

    public static void init(JavaPlugin plugin) {
        ConfigManager.plugin = plugin;
        config = plugin.getConfig();

        config.addDefault("options.glow", true);
        CONFIGS_ARGS.put("options.glow", Arrays.asList("true", "false"));
        config.addDefault("options.invulnerable", false);
        CONFIGS_ARGS.put("options.invulnerable", Arrays.asList("true", "false"));
        config.addDefault("options.pickup-delay", 0);
        CONFIGS_ARGS.put("options.pickup-delay", Collections.emptyList()); //
        config.addDefault("options.default-glow-color", "AQUA");
        CONFIGS_ARGS.put("options.default-glow-color", Constants.ALL_COLORS.keySet().stream().toList());
        config.addDefault("options.per-player-glow", true);
        CONFIGS_ARGS.put("options.per-player-glow", Arrays.asList("true", "false"));
        config.addDefault("options.per-player-protection", false);
        CONFIGS_ARGS.put("options.per-player-protection", Arrays.asList("true", "false"));
        config.addDefault("options.database-format", "file");
        CONFIGS_ARGS.put("options.database-format", Arrays.stream(DatabaseType.values()).map(val -> val.toString().toLowerCase()).collect(Collectors.toList()));
        config.addDefault("options.enable-bstats", true);
        CONFIGS_ARGS.put("options.enable-bstats", Arrays.asList("true", "false"));
        config.addDefault("options.enable-auto-update-checker", true);
        CONFIGS_ARGS.put("options.enable-auto-update-checker", Arrays.asList("true", "false"));
        config.addDefault("options.trash-confirm-close", true);
        CONFIGS_ARGS.put("options.trash-confirm-close", Arrays.asList("true", "false"));
        config.addDefault("options.allow-close-confirm-with-escape", false);
        CONFIGS_ARGS.put("options.allow-close-confirm-with-escape", Arrays.asList("true", "false"));

        config.addDefault("worlds.is-blacklist", true);
        CONFIGS_ARGS.put("worlds.is-blacklist", Arrays.asList("true", "false"));
        config.addDefault("worlds.list", Collections.singletonList("someworld"));
        CONFIGS_ARGS.put("worlds.list", Collections.emptyList()); //

        config.addDefault("protection.item-drop.enable", true);
        CONFIGS_ARGS.put("protection.item-drop.enable", Arrays.asList("true", "false"));
        config.addDefault("protection.item-drop.player-default", true);
        CONFIGS_ARGS.put("protection.item-drop.player-default", Arrays.asList("true", "false"));
        config.addDefault("protection.block-break.enable", true);
        CONFIGS_ARGS.put("protection.block-break.enable", Arrays.asList("true", "false"));
        config.addDefault("protection.block-break.player-default", true);
        CONFIGS_ARGS.put("protection.block-break.player-default", Arrays.asList("true", "false"));
        config.addDefault("protection.item-frame-drop.enable", true);
        CONFIGS_ARGS.put("protection.item-frame-drop.enable", Arrays.asList("true", "false"));
        config.addDefault("protection.item-frame-drop.player-default", true);
        CONFIGS_ARGS.put("protection.item-frame-drop.player-default", Arrays.asList("true", "false"));
        config.addDefault("protection.vehicle-destroy.enable", true);
        CONFIGS_ARGS.put("protection.vehicle-destroy.enable", Arrays.asList("true", "false"));
        config.addDefault("protection.vehicle-destroy.player-default", true);
        CONFIGS_ARGS.put("protection.vehicle-destroy.player-default", Arrays.asList("true", "false"));
        config.addDefault("protection.hanging-break.enable", true);
        CONFIGS_ARGS.put("protection.hanging-break.enable", Arrays.asList("true", "false"));
        config.addDefault("protection.hanging-break.player-default", true);
        CONFIGS_ARGS.put("protection.hanging-break.player-default", Arrays.asList("true", "false"));
        config.addDefault("protection.entity-kill.enable", true);
        CONFIGS_ARGS.put("protection.entity-kill.enable", Arrays.asList("true", "false"));
        config.addDefault("protection.entity-kill.player-default", true);
        CONFIGS_ARGS.put("protection.entity-kill.player-default", Arrays.asList("true", "false"));
        config.addDefault("protection.player-death.enable", false);
        CONFIGS_ARGS.put("protection.player-death.enable", Arrays.asList("true", "false"));
        config.addDefault("protection.player-death.player-default", false);
        CONFIGS_ARGS.put("protection.player-death.player-default", Arrays.asList("true", "false"));
        config.addDefault("protection.mythic-mobs.enable", false);
        CONFIGS_ARGS.put("protection.mythic-mobs.enable", Arrays.asList("true", "false"));
        config.addDefault("protection.mythic-mobs.player-default", true);
        CONFIGS_ARGS.put("protection.mythic-mobs.player-default", Arrays.asList("true", "false"));

        config.addDefault("messages.commands.player-only", "&cYou must be a player to use this command.");
        CONFIGS_ARGS.put("messages.commands.player-only", Collections.emptyList());
        config.addDefault("messages.commands.unknown", "&cUnknown subcommand &4&o{subcommand}");
        CONFIGS_ARGS.put("messages.commands.unknown", Collections.singletonList("{subcommand}"));
        config.addDefault("messages.commands.no-permission", "&cYou do not have permission to execute subcommand &4&o{subcommand}");
        CONFIGS_ARGS.put("messages.commands.no-permission", Collections.singletonList("{subcommand}"));
        config.addDefault("messages.commands.glow.set", "&eGlow color set to &o{color}");
        CONFIGS_ARGS.put("messages.commands.glow.set", Collections.singletonList("{color}"));
        config.addDefault("messages.commands.glow.invalid", "&eGlow color &o&6{color}&e is not valid");
        CONFIGS_ARGS.put("messages.commands.glow.invalid", Collections.singletonList("{color}"));
        config.addDefault("messages.commands.trash.container-title", "&cTrash bin");
        CONFIGS_ARGS.put("messages.commands.trash.container-title", Collections.emptyList());
        config.addDefault("messages.commands.reloaded", "&aMyDrops configuration has been reloaded successfully!");
        CONFIGS_ARGS.put("messages.commands.reloaded", Collections.emptyList());
        config.addDefault("messages.commands.config.invalid-key", "&cInvalid config key &4&o{key}");
        CONFIGS_ARGS.put("messages.commands.config.invalid-key", Collections.singletonList("{key}"));
        config.addDefault("messages.commands.config.invalid-value", "&cInvalid config value &4&o{value}&c for key &4&o{key}");
        CONFIGS_ARGS.put("messages.commands.config.invalid-value", Arrays.asList("{value}", "{key}"));
        config.addDefault("messages.commands.config.pos-value", "&cValue needs to be a positive number for key &4&o{key}");
        CONFIGS_ARGS.put("messages.commands.config.pos-value", Collections.singletonList("{key}"));
        config.addDefault("messages.commands.config.success", "&2&o{key}&a has been changed to &2&o{value}");
        CONFIGS_ARGS.put("messages.commands.config.success", Arrays.asList("{value}", "{key}"));
        config.addDefault("messages.commands.config.added", "&2&o{value}&a has been added to &2&o{key}");
        CONFIGS_ARGS.put("messages.commands.config.added", Arrays.asList("{value}", "{key}"));
        config.addDefault("messages.commands.config.removed", "&4&o{value}&c has been removed from &4&o{key}");
        CONFIGS_ARGS.put("messages.commands.config.removed", Arrays.asList("{value}", "{key}"));
        config.addDefault("messages.commands.config.value", "&bValue of &3&o{key}&b is &3&o{value}");
        CONFIGS_ARGS.put("messages.commands.config.value", Arrays.asList("{value}", "{key}"));
        config.addDefault("messages.commands.config.usage", "&cUsage: /{cmd} config <key> [value]");
        CONFIGS_ARGS.put("messages.commands.config.usage", Collections.singletonList("{cmd}"));
        config.addDefault("messages.commands.config.type", "&bPlease type the new value in chat | &cType cancel to exit");
        CONFIGS_ARGS.put("messages.commands.config.type", Collections.emptyList());
        config.addDefault("messages.commands.config.placeholders", "&bPlaceholders: &3{placeholders}");
        CONFIGS_ARGS.put("messages.commands.config.placeholders", Collections.singletonList("{placeholders}"));
        config.addDefault("messages.commands.config.current-value", "&bCurrent value: &3{value}");
        CONFIGS_ARGS.put("messages.commands.config.current-value", Collections.singletonList("{value}"));
        config.addDefault("messages.commands.config.input-cancelled", "&cInput has been cancelled");
        CONFIGS_ARGS.put("messages.commands.config.input-cancelled", Collections.emptyList());
        config.addDefault("messages.commands.config.right-click", "&7Right click to decrease");
        CONFIGS_ARGS.put("messages.commands.config.right-click", Collections.emptyList());
        config.addDefault("messages.commands.config.left-click", "&7Left click to increase");
        CONFIGS_ARGS.put("messages.commands.config.left-click", Collections.emptyList());
        config.addDefault("messages.commands.config.shift-click", "&7Shift + click to increase/decrease by 5");
        CONFIGS_ARGS.put("messages.commands.config.shift-click", Collections.emptyList());
        config.addDefault("messages.commands.invalid-item", "&cYou need to be holding a valid item in order to use this command");
        CONFIGS_ARGS.put("messages.commands.invalid-item", Collections.emptyList());
        config.addDefault("messages.gui.confirmation.title", "&6Confirm?");
        CONFIGS_ARGS.put("messages.gui.confirmation.title", Collections.emptyList());
        config.addDefault("messages.gui.config.title", "&aConfiguration - {key}");
        CONFIGS_ARGS.put("messages.gui.config.title", Collections.singletonList("{key}"));
        config.addDefault("messages.gui.color.title", "&bSelect a color");
        CONFIGS_ARGS.put("messages.gui.color.title", Collections.emptyList());

        defaultAcceptItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta acceptMeta = defaultAcceptItem.getItemMeta();
        acceptMeta.setDisplayName("&aAccept");
        defaultAcceptItem.setItemMeta(acceptMeta);

        defaultDeclineItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta declineMeta = defaultDeclineItem.getItemMeta();
        declineMeta.setDisplayName("&cDecline");
        defaultDeclineItem.setItemMeta(declineMeta);

        config.addDefault("items.confirmation.accept", defaultAcceptItem);
        CONFIGS_ARGS.put("items.confirmation.accept", Collections.singletonList("reset"));
        config.addDefault("items.confirmation.decline", defaultDeclineItem);
        CONFIGS_ARGS.put("items.confirmation.decline", Collections.singletonList("reset"));

        defaultBackItem = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = defaultBackItem.getItemMeta();
        backMeta.setDisplayName("&cBack");
        defaultBackItem.setItemMeta(backMeta);

        defaultCloseItem = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = defaultCloseItem.getItemMeta();
        closeMeta.setDisplayName("&cClose");
        defaultCloseItem.setItemMeta(closeMeta);

        config.addDefault("items.gui.back", defaultBackItem);
        CONFIGS_ARGS.put("items.gui.back",  Collections.singletonList("reset"));
        config.addDefault("items.gui.close", defaultCloseItem);
        CONFIGS_ARGS.put("items.gui.close", Collections.singletonList("reset"));

        config.options().copyDefaults(true);
        plugin.saveConfig();

        registerEventHandlers();
    }

    // Register event handlers here because of reload command
    private static void registerEventHandlers()
    {
        Bukkit.getServer().getPluginManager().registerEvents(Utils.getGuiManager(), plugin);

        Bukkit.getServer().getPluginManager().registerEvents(new ProtectionHandler(), plugin);

        if (ConfigManager.hasItemDropProtection())
            Bukkit.getServer().getPluginManager().registerEvents(new ItemDropHandler(), plugin);

        if (ConfigManager.hasBlockBreakProtection())
            Bukkit.getServer().getPluginManager().registerEvents(new BlockBreakHandler(), plugin);

        if (ConfigManager.hasVehicleDestroyProtection())
            Bukkit.getServer().getPluginManager().registerEvents(new VehicleDestroyHandler(), plugin);

        if (ConfigManager.hasHangingBreakProtection())
            Bukkit.getServer().getPluginManager().registerEvents(new HangingBreakHandler(), plugin);

        if (ConfigManager.hasItemFrameDropProtection())
            Bukkit.getServer().getPluginManager().registerEvents(new ItemFrameDropHandler(), plugin);

        if (ConfigManager.hasEntityKillProtection())
            Bukkit.getServer().getPluginManager().registerEvents(new EntityKillHandler(), plugin);

        if (ConfigManager.hasPlayerDeathProtection())
            Bukkit.getServer().getPluginManager().registerEvents(new PlayerDeathHandler(), plugin);

        if (ConfigManager.hasMythicMobsProtection() && Bukkit.getServer().getPluginManager().isPluginEnabled("MythicMobs")) {
            getLogger().info("MythicMobs is enabled, hooking into it for event handling");
            Bukkit.getServer().getPluginManager().registerEvents(new MythicMobsHandler(), plugin);
        }

        if (ConfigManager.hasAutoUpdateChecker())
            Bukkit.getServer().getPluginManager().registerEvents(new AutoUpdaterHandler(), plugin);
    }

    public static void reload() {
        HandlerList.unregisterAll(plugin);
        plugin.reloadConfig();
        CONFIGS_ARGS.clear();
        clearCache();
        init(plugin);
    }

    private static void clearCache() {
        databaseType = null;
        msgCmdPlayerOnly = null;
        msgCmdReloaded = null;
        msgCmdInvalidItem = null;
        msgCmdTrashTitle = null;
        msgCmdConfigInvalidKey = null;
        msgCmdConfigInvalidValue = null;
        msgCmdConfigPosValue = null;
        msgCmdConfigSuccess = null;
        msgCmdConfigUsage = null;
        msgCmdConfigAdded = null;
        msgCmdConfigRemoved = null;
        txtConfirmTitle = null;
        txtConfigGUITitle = null;
        glowColor = null;
        defaultAcceptItem = null;
        acceptItem = null;
        defaultDeclineItem = null;
        declineItem = null;
        defaultBackItem = null;
        backItem = null;
        defaultCloseItem = null;
        closeItem = null;
    }

    public static DatabaseType getDatabaseType()
    {
        if (databaseType == null)
            databaseType = DatabaseType.valueOf(config.getString("options.database-format", "file").toUpperCase());

        return databaseType;
    }

    public static boolean canCloseConfirmWithEscape() {
        return config.getBoolean("options.allow-close-confirm-with-escape", false);
    }

    public static boolean hasOptionGlow() {
        return config.getBoolean("options.glow", true);
    }

    public static boolean hasOptionInvulnerable()
    {
        return config.getBoolean("options.invulnerable", false);
    }

    public static int getPickupDelay()
    {
        return config.getInt("options.pickup-delay", 0);
    }

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

    public static boolean hasMythicMobsProtection() {
        return config.getBoolean("protection.mythic-mobs.enable", false);
    }

    public static boolean hasBStats()
    {
        return config.getBoolean("options.enable-bstats", true);
    }

    public static boolean hasTrashConfirmClose() {
        return config.getBoolean("options.trash-confirm-close", true);
    }

    public static String getMsgCmdPlayerOnly()
    {
        if (msgCmdPlayerOnly == null)
            msgCmdPlayerOnly = Utils.colorize(config.getString("messages.commands.player-only", "You must be a player to use this command."));

        return msgCmdPlayerOnly;
    }

    public static String getMsgCmdInvalidItem()
    {
        if (msgCmdInvalidItem == null)
            msgCmdInvalidItem = Utils.colorize(config.getString("messages.commands.invalid-item", "&cYou need to be holding a valid item in order to use this command"));

        return msgCmdInvalidItem;
    }

    public static String getMsgCmdConfigInvalidKey()
    {
        if (msgCmdConfigInvalidKey == null)
            msgCmdConfigInvalidKey = Utils.colorize(config.getString("messages.commands.config.invalid-key", "&cInvalid config key &4&o{key}"));

        return msgCmdConfigInvalidKey;
    }

    public static String getMsgCmdConfigInvalidValue() {
        if (msgCmdConfigInvalidValue == null)
            msgCmdConfigInvalidValue = Utils.colorize(config.getString("messages.commands.config.invalid-value", "&cInvalid config value &4&o{value}&c for key &4&o{key}"));

        return msgCmdConfigInvalidValue;
    }

    public static String getMsgCmdConfigPosValue() {
        if (msgCmdConfigPosValue == null)
            msgCmdConfigPosValue = Utils.colorize(config.getString("messages.commands.config.pos-value", "&cValue needs to be a positive number for key &4&o{key}"));

        return msgCmdConfigPosValue;
    }

    public static String getMsgCmdConfigSuccess() {
        if (msgCmdConfigSuccess == null)
            msgCmdConfigSuccess = Utils.colorize(config.getString("messages.commands.config.success", "&2&o{key}&a has been changed to &2{value}"));

        return msgCmdConfigSuccess;
    }

    public static String getMsgCmdConfigUsage() {
        if (msgCmdConfigUsage == null)
            msgCmdConfigUsage = Utils.colorize(config.getString("messages.commands.config.usage", "&cUsage: /{cmd} config <key> [value]"));

        return msgCmdConfigUsage;
    }

    public static String getMsgCmdConfigAdded() {
        if (msgCmdConfigAdded == null)
            msgCmdConfigAdded = Utils.colorize(config.getString("messages.commands.config.added", "&2&o{value}&a has been added to &2&o{key}"));

        return msgCmdConfigAdded;
    }

    public static String getMsgCmdConfigRemoved() {
        if (msgCmdConfigRemoved == null)
            msgCmdConfigRemoved = Utils.colorize(config.getString("messages.commands.config.removed", "&4&o{value}&c has been removed from &4&o{key}"));

        return msgCmdConfigRemoved;
    }

    public static String getMsgCmdConfigValue() {
        if (msgCmdConfigValue == null)
            msgCmdConfigValue = Utils.colorize(config.getString("messages.commands.config.value", "&bValue of &3&o{key}&b is &3&o{value}"));

        return msgCmdConfigValue;
    }

    public static String getMsgCmdConfigType() {
        if (msgCmdConfigType == null)
            msgCmdConfigType = Utils.colorize(config.getString("messages.commands.config.type", "&bPlease type the new value in chat | &cType cancel to exit"));

        return msgCmdConfigType;
    }

    public static String getMsgCmdConfigPlaceholders() {
        if (msgCmdConfigPlaceholders == null)
            msgCmdConfigPlaceholders = Utils.colorize(config.getString("messages.commands.config.placeholders", "&bPlaceholders: &3{placeholders}"));

        return msgCmdConfigPlaceholders;
    }

    public static String getMsgCmdConfigCurrentValue() {
        if (msgCmdConfigCurrentValue == null)
            msgCmdConfigCurrentValue = Utils.colorize(config.getString("messages.commands.config.current-value", "&bCurrent value: &3{value}"));

        return msgCmdConfigCurrentValue;
    }

    public static String getMsgCmdConfigInputCancelled() {
        if (msgCmdConfigInputCancelled == null)
            msgCmdConfigInputCancelled = Utils.colorize(config.getString("messages.commands.config.input-cancelled", "&cInput has been cancelled"));

        return msgCmdConfigInputCancelled;
    }

    public static String getMsgCmdConfigRightClick() {
        if (msgCmdConfigRightClick == null)
            msgCmdConfigRightClick = Utils.colorize(config.getString("messages.commands.config.right-click", "&7Right click to decrease"));

        return msgCmdConfigRightClick;
    }

    public static String getMsgCmdConfigLeftClick() {
        if (msgCmdConfigLeftClick == null)
            msgCmdConfigLeftClick = Utils.colorize(config.getString("messages.commands.config.left-click", "&7Left click to increase"));

        return msgCmdConfigLeftClick;
    }

    public static String getMsgCmdConfigShiftClick() {
        if (msgCmdConfigShiftClick == null)
            msgCmdConfigShiftClick = Utils.colorize(config.getString("messages.commands.config.shift-click", "&7Shift + click to increase/decrease by 5"));

        return msgCmdConfigShiftClick;
    }

    public static String getMsgCmdTrashTitle()
    {
        if (msgCmdTrashTitle == null)
            msgCmdTrashTitle = Utils.colorize(config.getString("messages.commands.trash.container-title", "&cTrash bin"));

        return msgCmdTrashTitle;
    }

    public static String getMsgCmdReloaded()
    {
        if (msgCmdReloaded == null)
            msgCmdReloaded = Utils.colorize(config.getString("messages.commands.reloaded", "&aMyDrops configuration has been reloaded successfully!"));

        return msgCmdReloaded;
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

    public static boolean hasPerPlayerProtection()
    {
        return false; //TODO config.getBoolean("options.per-player-protection", false);
    }

    public static boolean isWorldListBlacklist()
    {
        return config.getBoolean("worlds.is-blacklist", true);
    }

    public static List<String> getWorldList()
    {
        return config.getStringList("worlds.list");
    }

    public static boolean hasAutoUpdateChecker()
    {
        return config.getBoolean("options.enable-auto-update-checker", true);
    }

    public static String getTxtConfirmTitle()
    {
        if (txtConfirmTitle == null)
            txtConfirmTitle = Utils.colorize(config.getString("messages.gui.confirmation.title", "&6Confirm?"));

        return txtConfirmTitle;
    }

    public static String getTxtConfigGUITitle() {
        if (txtConfigGUITitle == null)
            txtConfigGUITitle = Utils.colorize(config.getString("messages.gui.config.title", "&aConfiguration - {key}"));

        return txtConfigGUITitle;
    }

    public static String getTxtColorGUITitle() {
        if (txtColorGUITitle == null)
            txtColorGUITitle = Utils.colorize(config.getString("messages.gui.color.title", "&bSelect a color"));

        return txtColorGUITitle;
    }

    public static ItemStack getAcceptItem() {
        if (acceptItem == null)
            acceptItem = config.getItemStack("items.confirmation.accept", defaultAcceptItem);

        Utils.colorizeItem(acceptItem);

        return acceptItem;
    }

    public static ItemStack getDefaultAcceptItem() {
        return defaultAcceptItem;
    }

    public static ItemStack getDeclineItem() {
        if (declineItem == null)
            declineItem = config.getItemStack("items.confirmation.decline", defaultDeclineItem);

        Utils.colorizeItem(declineItem);

        return declineItem;
    }

    public static ItemStack getDefaultDeclineItem() {
        return defaultDeclineItem;
    }

    public static ItemStack getBackItem() {
        if (backItem == null)
            backItem = config.getItemStack("items.gui.back", defaultBackItem);

        Utils.colorizeItem(backItem);

        return backItem;
    }

    public static ItemStack getDefaultBackItem() {
        return defaultBackItem;
    }

    public static ItemStack getCloseItem() {
        if (closeItem == null)
            closeItem = config.getItemStack("items.gui.close", defaultCloseItem);

        Utils.colorizeItem(closeItem);

        return closeItem;
    }

    public static ItemStack getDefaultCloseItem() {
        return defaultCloseItem;
    }

    public static void updateValue(String key, Object value)
    {
        config.set(key, value);
        plugin.saveConfig();
        reload();
    }

    public static List<String> getGlobalKeys() {
        return new ArrayList<>(config.getKeys(false));
    }

    public static List<String> getKeys(String key) {
        var keys = new ArrayList<>(config.getConfigurationSection(key).getKeys(true));
        keys.removeIf(k -> getValue(key + "." + k) instanceof ConfigurationSection);
        return keys;
    }

    public static Object getValue(String key) {
        return config.get(key);
    }
}