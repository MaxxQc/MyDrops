package net.maxxqc.mydrops.utils;

import net.maxxqc.mydrops.databases.IDatabase;
import net.maxxqc.mydrops.databases.types.FileDatabase;
import net.maxxqc.mydrops.databases.types.MongoDB;
import net.maxxqc.mydrops.databases.types.MySQL;
import net.maxxqc.mydrops.databases.types.SQLite;
import net.maxxqc.mydrops.events.AutoUpdaterHandler;
import net.maxxqc.mydrops.events.HideItemsHandler;
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

public class ConfigManager {
    private static JavaPlugin plugin;
    private static FileConfiguration config;

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
    private static String msgCmdProtectionInvalidValue;

    private static String msgHelpHeader;
    private static String msgHelpCore;
    private static String msgHelpGlowcolor;
    private static String msgHelpProtection;
    private static String msgHelpProtectionList;
    private static String msgHelpTrash;
    private static String msgHelpTrust;
    private static String msgHelpConfig;
    private static String msgHelpReload;

    private static String msgCmdTrustUsage;
    private static String msgCmdProtectionUsage;
    private static String msgCmdAddRemoveUsage;
    private static String msgCmdAddSuccess;
    private static String msgCmdAddSuccessParty;
    private static String msgCmdRemoveSuccess;
    private static String msgCmdRemoveSuccessParty;
    private static String msgCmdTrustAlreadyTrusted;
    private static String msgCmdTrustAlreadyTrustedParty;
    private static String msgCmdTrustNotTrusted;
    private static String msgCmdTrustNotTrustedParty;
    private static String msgCmdTrustList;
    private static String msgCmdTrustSelf;
    private static String msgCmdProtectionInvalidType;
    private static String msgCmdProtectionSet;
    private static String msgCmdProtectionList;

    private static String txtConfirmTitle;
    private static String txtConfigGUITitle;
    private static String txtColorGUITitle;
    private static String msgPlayerNotFound;
    private static String msgYourParty;
    private static String msgDefault;
    private static String msgEnabled;
    private static String msgDisabled;

    private static String databaseFormat;

    private static ChatColor glowColor;

    private static ItemStack defaultAcceptItem;
    private static ItemStack acceptItem;
    private static ItemStack defaultDeclineItem;
    private static ItemStack declineItem;
    private static ItemStack defaultBackItem;
    private static ItemStack backItem;
    private static ItemStack defaultCloseItem;
    private static ItemStack closeItem;

    private static ItemStack defaultNoneItem;
    private static ItemStack noneItem;

    private static IDatabase database;

    public static Map<String, List<String>> CONFIGS_ARGS = new HashMap<>();

    public static void init(JavaPlugin plugin) {
        ConfigManager.plugin = plugin;
        config = plugin.getConfig();

        config.addDefault("options.glow", true);
        CONFIGS_ARGS.put("options.glow", Arrays.asList("true", "false"));
        config.addDefault("options.invulnerable", false);
        CONFIGS_ARGS.put("options.invulnerable", Arrays.asList("true", "false"));
        config.addDefault("options.pickup-delay", 0);
        CONFIGS_ARGS.put("options.pickup-delay", Collections.emptyList());
        config.addDefault("options.protection-expiry", 0);
        CONFIGS_ARGS.put("options.protection-expiry", Collections.emptyList());
        config.addDefault("options.default-glow-color", "AQUA");
        CONFIGS_ARGS.put("options.default-glow-color", Constants.ALL_COLORS.keySet().stream().toList());
        config.addDefault("options.per-player-glow", true);
        CONFIGS_ARGS.put("options.per-player-glow", Arrays.asList("true", "false"));
        config.addDefault("options.per-player-protection", false);
        CONFIGS_ARGS.put("options.per-player-protection", Arrays.asList("true", "false"));
        config.addDefault("options.hide-drops-from-others", true);
        CONFIGS_ARGS.put("options.hide-drops-from-others", Arrays.asList("true", "false"));
        config.addDefault("options.database-format", "sqlite");
        CONFIGS_ARGS.put("options.database-format", new ArrayList<>(Constants.DATABASE_FORMATS));
        config.addDefault("options.enable-bstats", true);
        CONFIGS_ARGS.put("options.enable-bstats", Arrays.asList("true", "false"));
        config.addDefault("options.enable-auto-update-checker", true);
        CONFIGS_ARGS.put("options.enable-auto-update-checker", Arrays.asList("true", "false"));
        config.addDefault("options.trash-confirm-close", true);
        CONFIGS_ARGS.put("options.trash-confirm-close", Arrays.asList("true", "false"));
        config.addDefault("options.allow-close-confirm-with-escape", false);
        CONFIGS_ARGS.put("options.allow-close-confirm-with-escape", Arrays.asList("true", "false"));
        config.addDefault("hooks.parties.enabled", true);
        CONFIGS_ARGS.put("hooks.parties.enabled", Arrays.asList("true", "false"));
        config.addDefault("hooks.parties.players-trust-own-party-by-default", false);
        CONFIGS_ARGS.put("hooks.parties.players-trust-own-party-by-default", Arrays.asList("true", "false"));
        config.addDefault("hooks.mythicmobs.enabled", true);
        CONFIGS_ARGS.put("hooks.mythicmobsenabled.", Arrays.asList("true", "false"));

        config.addDefault("database.host", "127.0.0.1");
        CONFIGS_ARGS.put("database.host", Collections.emptyList());
        config.addDefault("database.user", "minecraft");
        CONFIGS_ARGS.put("database.user", Collections.emptyList());
        config.addDefault("database.password", "pass");
        CONFIGS_ARGS.put("database.password", Collections.emptyList());
        config.addDefault("database.port", "3306");
        CONFIGS_ARGS.put("database.port", Collections.emptyList());
        config.addDefault("database.database-name", "database");
        CONFIGS_ARGS.put("database.database-name", Collections.emptyList());
        config.addDefault("database.tables-prefix", "mydrops_");
        CONFIGS_ARGS.put("database.tables-prefix", Collections.emptyList());
        config.addDefault("database.uri", "mongodb://localhost:27017");
        CONFIGS_ARGS.put("database.uri", Collections.emptyList());

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
        config.addDefault("messages.commands.config.usage", "&cUsage: /{cmd} config [key] [value]");
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
        config.addDefault("messages.commands.trust.usage", "&cUsage: /{cmd} trust <add/remove/list> [player]");
        CONFIGS_ARGS.put("messages.commands.trust.usage", Collections.singletonList("{cmd}"));
        config.addDefault("messages.commands.protection.usage", "&cUsage: /{cmd} protection <protection type/list> [true/false]");
        CONFIGS_ARGS.put("messages.commands.protection.usage", Collections.singletonList("{cmd}"));
        config.addDefault("messages.commands.trust.add-remove-usage", "&cUsage: /{cmd} trust {subcmd} <player>");
        CONFIGS_ARGS.put("messages.commands.trust.add-remove-usage", Arrays.asList("{cmd}", "{subcmd}"));
        config.addDefault("messages.commands.trust.add-success", "&2{player}&a has been added to your trusted list");
        CONFIGS_ARGS.put("messages.commands.trust.add-success", Collections.singletonList("{player}"));
        config.addDefault("messages.commands.trust.add-success-party", "&2{party}&a has been added to your trusted list");
        CONFIGS_ARGS.put("messages.commands.trust.add-success-party", Collections.singletonList("{party}"));
        config.addDefault("messages.commands.trust.remove-success", "&2{player}&a has been removed from your trusted list");
        CONFIGS_ARGS.put("messages.commands.trust.remove-success", Collections.singletonList("{player}"));
        config.addDefault("messages.commands.trust.remove-success-party", "&2{party}&a has been removed from your trusted list");
        CONFIGS_ARGS.put("messages.commands.trust.remove-success-party", Collections.singletonList("{party}"));
        config.addDefault("messages.commands.trust.already-trusted", "&4{player}&c is already part of your trusted list");
        CONFIGS_ARGS.put("messages.commands.trust.already-trusted", Collections.singletonList("{player}"));
        config.addDefault("messages.commands.trust.already-trusted-party", "&4{party}&c is already part of your trusted list");
        CONFIGS_ARGS.put("messages.commands.trust.already-trusted-party", Collections.singletonList("{party}"));
        config.addDefault("messages.commands.trust.not-trusted", "&4{player}&c is not part of your trusted list");
        CONFIGS_ARGS.put("messages.commands.trust.not-trusted", Collections.singletonList("{player}"));
        config.addDefault("messages.commands.trust.not-trusted-party", "&4{party}&c is not part of your trusted list");
        CONFIGS_ARGS.put("messages.commands.trust.not-trusted-party", Collections.singletonList("{party}"));
        config.addDefault("messages.commands.trust.list", "&eTrusted players ({count}): &6{players}");
        CONFIGS_ARGS.put("messages.commands.trust.list", Arrays.asList("{count}", "{players}"));
        config.addDefault("messages.commands.trust.self", "&cYou cannot add yourself to your trusted list");
        CONFIGS_ARGS.put("messages.commands.trust.self", Collections.emptyList());
        config.addDefault("messages.commands.protection.invalid-type", "&cInvalid protection type: &4{type}");
        CONFIGS_ARGS.put("messages.commands.protection.invalid-type", Collections.singletonList("{type}"));
        config.addDefault("messages.commands.protection.set", "&aProtection type &2{type} &ahas been {status}");
        CONFIGS_ARGS.put("messages.commands.protection.set", Arrays.asList("{type}", "{status}"));
        config.addDefault("messages.commands.protection.list", "&7Your protections: {protections}");
        CONFIGS_ARGS.put("messages.commands.protection.list", Collections.singletonList("{protections}"));
        config.addDefault("messages.commands.protection.invalid-value", "&cInvalid value &4&o{value} &cfor protection type &4&o{type}");
        CONFIGS_ARGS.put("messages.commands.protection.invalid-value", Arrays.asList("{type}", "{value}"));
        config.addDefault("messages.gui.confirmation.title", "&6Confirm?");
        CONFIGS_ARGS.put("messages.gui.confirmation.title", Collections.emptyList());
        config.addDefault("messages.gui.config.title", "&aConfiguration - {key}");
        CONFIGS_ARGS.put("messages.gui.config.title", Collections.singletonList("{key}"));
        config.addDefault("messages.gui.color.title", "&bSelect a color");
        CONFIGS_ARGS.put("messages.gui.color.title", Collections.emptyList());
        config.addDefault("messages.player-not-found", "&cPlayer &4{player}&c not found");
        CONFIGS_ARGS.put("messages.player-not-found", Collections.singletonList("{player}"));
        config.addDefault("messages.empty", "empty");
        CONFIGS_ARGS.put("messages.empty", Collections.emptyList());
        config.addDefault("messages.your-party", "Your party");
        CONFIGS_ARGS.put("messages.your-party", Collections.emptyList());
        config.addDefault("messages.selected-suffix", " (selected)");
        CONFIGS_ARGS.put("messages.selected-suffix", Collections.emptyList());
        config.addDefault("messages.default", "&b&oDefault value");
        CONFIGS_ARGS.put("messages.default", Collections.emptyList());
        config.addDefault("messages.enabled", "&2enabled");
        CONFIGS_ARGS.put("messages.enabled", Collections.emptyList());
        config.addDefault("messages.disabled", "&4disabled");
        CONFIGS_ARGS.put("messages.disabled", Collections.emptyList());

        config.addDefault("messages.help.header", "&eAll commands are:");
        CONFIGS_ARGS.put("messages.help.header", Collections.emptyList());
        config.addDefault("messages.help.core", "&6/{cmd}&e - Shows this help menu");
        CONFIGS_ARGS.put("messages.help.core", Collections.singletonList("{cmd}"));
        config.addDefault("messages.help.glowcolor", "&6/{cmd} glowcolor [color]&e - Defines a new glowing color for yourself or open the color selection GUI");
        CONFIGS_ARGS.put("messages.help.glowcolor", Collections.singletonList("{cmd}"));
        config.addDefault("messages.help.protection", "&6/{cmd} protection <protection type/list> [true/false]&e - Toggles a protection rule for yourself");
        CONFIGS_ARGS.put("messages.help.protection", Collections.singletonList("{cmd}"));
        config.addDefault("messages.help.protection.list", "&6/{cmd} protection list&e - List all your protections");
        CONFIGS_ARGS.put("messages.help.protection.list", Collections.emptyList());
        config.addDefault("messages.help.trash", "&6/{cmd} trash &e - Opens up a trash bin container");
        CONFIGS_ARGS.put("messages.help.trash", Collections.singletonList("{cmd}"));
        config.addDefault("messages.help.trust", "&6/{cmd} trust <add/addparty/remove/removeparty/list> [player] &e - Controls your trusted list");
        CONFIGS_ARGS.put("messages.help.trust", Collections.singletonList("{cmd}"));
        config.addDefault("messages.help.config", "&6/{cmd} config [key] [value] &e - Defines the value of a setting or open the config GUI");
        CONFIGS_ARGS.put("messages.help.config", Collections.singletonList("{cmd}"));
        config.addDefault("messages.help.reload", "&6/{cmd} reload &e - Reloads the configuration from file");
        CONFIGS_ARGS.put("messages.help.reload", Collections.singletonList("{cmd}"));

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

        defaultBackItem = Utils.createItemStack(Material.BARRIER, "&cBack");
        defaultCloseItem = Utils.createItemStack(Material.BARRIER, "&cClose");
        defaultNoneItem = Utils.createItemStack(Material.WHITE_WOOL, "&f&oNone");

        msgEnabled = config.getString("messages.enabled", "&2enabled");
        msgDisabled = config.getString("messages.disabled", "&4disabled");

        config.addDefault("items.gui.back", defaultBackItem);
        CONFIGS_ARGS.put("items.gui.back", Collections.singletonList("reset"));
        config.addDefault("items.gui.close", defaultCloseItem);
        CONFIGS_ARGS.put("items.gui.close", Collections.singletonList("reset"));
        config.addDefault("items.gui.none", defaultNoneItem);
        CONFIGS_ARGS.put("items.gui.none", Collections.singletonList("reset"));

        config.options().copyDefaults(true);
        plugin.saveConfig();

        registerEventHandlers();

        databaseFormat = config.getString("options.database-format", "sqlite").toLowerCase();

        switch (databaseFormat) {
            case "file":
                database = new FileDatabase();
                break;
            case "mysql":
                database = new MySQL();
                break;
            case "mongodb":
                database = new MongoDB();
                break;
            default:
                database = new SQLite();
        }

        database.load();
    }

    // Register event handlers here because of reload command
    // Will need to refactor this...
    private static void registerEventHandlers() {
        Bukkit.getServer().getPluginManager().registerEvents(Utils.getGuiManager(), plugin);

        Bukkit.getServer().getPluginManager().registerEvents(new ProtectionHandler(), plugin);

        if (hasServerProtection(ProtectionType.ITEM_DROP))
            Bukkit.getServer().getPluginManager().registerEvents(new ItemDropHandler(), plugin);

        if (hasServerProtection(ProtectionType.BLOCK_BREAK))
            Bukkit.getServer().getPluginManager().registerEvents(new BlockBreakHandler(), plugin);

        if (hasServerProtection(ProtectionType.VEHICLE_DESTROY))
            Bukkit.getServer().getPluginManager().registerEvents(new VehicleDestroyHandler(), plugin);

        if (hasServerProtection(ProtectionType.HANGING_BREAK))
            Bukkit.getServer().getPluginManager().registerEvents(new HangingBreakHandler(), plugin);

        if (hasServerProtection(ProtectionType.ITEM_FRAME_DROP))
            Bukkit.getServer().getPluginManager().registerEvents(new ItemFrameDropHandler(), plugin);

        if (hasServerProtection(ProtectionType.ENTITY_KILL))
            Bukkit.getServer().getPluginManager().registerEvents(new EntityKillHandler(), plugin);

        if (hasServerProtection(ProtectionType.PLAYER_DEATH))
            Bukkit.getServer().getPluginManager().registerEvents(new PlayerDeathHandler(), plugin);

        if (hasHookMythicMobs() && hasServerProtection(ProtectionType.MYTHIC_MOBS) && Bukkit.getServer().getPluginManager().isPluginEnabled("MythicMobs")) {
            plugin.getLogger().info("MythicMobs is enabled, hooking into it for event handling");
            Bukkit.getServer().getPluginManager().registerEvents(new MythicMobsHandler(), plugin);
        }

        if (getHideDropsFromOthers())
            Bukkit.getServer().getPluginManager().registerEvents(new HideItemsHandler(), plugin);

        if (hasAutoUpdateChecker())
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
        msgCmdPlayerOnly = null;
        msgCmdProtectionInvalidValue = null;
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
        msgCmdConfigValue = null;
        msgCmdConfigType = null;
        msgCmdConfigPlaceholders = null;
        msgCmdConfigCurrentValue = null;
        msgCmdConfigInputCancelled = null;
        msgCmdConfigRightClick = null;
        msgCmdConfigLeftClick = null;
        msgCmdConfigShiftClick = null;
        msgCmdTrustUsage = null;
        msgCmdProtectionUsage = null;
        msgCmdAddSuccess = null;
        msgCmdAddSuccessParty = null;
        msgCmdTrustAlreadyTrusted = null;
        msgCmdTrustAlreadyTrustedParty = null;
        msgCmdTrustList = null;
        msgCmdTrustSelf = null;
        msgCmdAddRemoveUsage = null;
        msgCmdTrustNotTrusted = null;
        msgCmdTrustNotTrustedParty = null;
        msgCmdRemoveSuccess = null;
        msgCmdRemoveSuccessParty = null;
        msgCmdProtectionInvalidType = null;
        msgCmdProtectionSet = null;
        msgCmdProtectionList = null;
        msgYourParty = null;
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
        defaultNoneItem = null;
        noneItem = null;
        msgPlayerNotFound = null;
        database = null;
        msgHelpHeader = null;
        msgHelpCore = null;
        msgHelpGlowcolor = null;
        msgHelpProtection = null;
        msgHelpProtectionList = null;
        msgHelpTrash = null;
        msgHelpTrust = null;
        msgHelpConfig = null;
        msgHelpReload = null;
        msgEnabled = null;
        msgDisabled = null;
    }

    public static IDatabase getDatabase() {
        return database;
    }

    public static String getDatabaseTablesPrefix() {
        return config.getString("database.tables-prefix", "mydrops_");
    }

    public static String getDatabaseHost() {
        return config.getString("database.host", "127.0.0.1");
    }

    public static String getDatabaseUser() {
        return config.getString("database.user", "minecraft");
    }

    public static String getDatabasePassword() {
        return config.getString("database.password", "pass");
    }

    public static String getDatabasePort() {
        return config.getString("database.port", "3306");
    }

    public static String getDatabaseName() {
        return config.getString("database.database-name", "database");
    }

    public static String getDatabaseUri() {
        return config.getString("database.uri", "mongodb://localhost:27017");
    }

    public static boolean canCloseConfirmWithEscape() {
        return config.getBoolean("options.allow-close-confirm-with-escape", false);
    }

    public static boolean hasOptionGlow() {
        return config.getBoolean("options.glow", true);
    }

    public static boolean hasOptionInvulnerable() {
        return config.getBoolean("options.invulnerable", false);
    }

    public static int getPickupDelay() {
        return config.getInt("options.pickup-delay", 0);
    }

    public static ChatColor getGlowColor() {
        if (glowColor == null) {
            try {
                glowColor = ChatColor.valueOf(config.getString("options.default-glow-color", "AQUA").toUpperCase());
            } catch (Exception e) {
                glowColor = ChatColor.AQUA;
                e.printStackTrace();
            }
        }

        return glowColor;
    }

    public static boolean hasPerPlayerGlow() {
        return config.getBoolean("options.glow", true) && config.getBoolean("options.per-player-glow", true);
    }

    public static int getProtectionExpiry() {
        return config.getInt("options.protection-expiry", 0);
    }

    public static boolean hasProtectionExpiry() {
        return config.getInt("options.protection-expiry", 0) > 0;
    }

    public static boolean hasServerProtection(ProtectionType type) {
        switch (type) {
            case BLOCK_BREAK -> {
                return config.getBoolean("protection.block-break.enable", true);
            }
            case ENTITY_KILL -> {
                return config.getBoolean("protection.entity-kill.enable", true);
            }
            case HANGING_BREAK -> {
                return config.getBoolean("protection.hanging-break.enable", true);
            }
            case ITEM_DROP -> {
                return config.getBoolean("protection.item-drop.enable", true);
            }
            case ITEM_FRAME_DROP -> {
                return config.getBoolean("protection.item-frame-drop.enable", true);
            }
            case PLAYER_DEATH -> {
                return config.getBoolean("protection.player-death.enable", false);
            }
            case VEHICLE_DESTROY -> {
                return config.getBoolean("protection.vehicle-destroy.enable", true);
            }
            case MYTHIC_MOBS -> {
                return config.getBoolean("protection.mythic-mobs.enable", false);
            }
            default -> {
                return false;
            }
        }
    }

    public static boolean hasBStats() {
        return config.getBoolean("options.enable-bstats", true);
    }

    public static boolean hasTrashConfirmClose() {
        return config.getBoolean("options.trash-confirm-close", true);
    }

    public static String getMsgEmpty() {
        return config.getString("messages.empty", "empty");
    }

    public static String getMsgCmdPlayerOnly() {
        if (msgCmdPlayerOnly == null)
            msgCmdPlayerOnly = Utils.colorize(config.getString("messages.commands.player-only", "You must be a player to use this command."));

        return msgCmdPlayerOnly;
    }

    public static String getMsgPlayerNotFound() {
        if (msgPlayerNotFound == null)
            msgPlayerNotFound = Utils.colorize(config.getString("messages.player-not-found", "&cPlayer &4{player}&c not found"));

        return msgPlayerNotFound;
    }

    public static String getMsgCmdInvalidItem() {
        if (msgCmdInvalidItem == null)
            msgCmdInvalidItem = Utils.colorize(config.getString("messages.commands.invalid-item", "&cYou need to be holding a valid item in order to use this command"));

        return msgCmdInvalidItem;
    }

    public static String getMsgCmdConfigInvalidKey() {
        if (msgCmdConfigInvalidKey == null)
            msgCmdConfigInvalidKey = Utils.colorize(config.getString("messages.commands.config.invalid-key", "&cInvalid config key &4&o{key}"));

        return msgCmdConfigInvalidKey;
    }

    public static String getMsgCmdConfigInvalidValue() {
        if (msgCmdConfigInvalidValue == null)
            msgCmdConfigInvalidValue = Utils.colorize(config.getString("messages.commands.config.invalid-value", "&cInvalid config value &4&o{value}&c for key &4&o{key}"));

        return msgCmdConfigInvalidValue;
    }

    public static String getMsgCmdProtectionInvalidValue(ProtectionType protectionType, String input)
    {
        if (msgCmdProtectionInvalidValue == null)
            msgCmdProtectionInvalidValue = Utils.colorize(config.getString("messages.commands.protection.invalid-value", "&cInvalid value &4&o{value} &cfor protection type &4&o{type}"));

        return msgCmdProtectionInvalidValue.replace("{type}", protectionType.getStringValue()).replace("{value}", input);
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
            msgCmdConfigUsage = Utils.colorize(config.getString("messages.commands.config.usage", "&cUsage: /{cmd} config [key] [value]"));

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

    public static String getMsgCmdTrashTitle() {
        if (msgCmdTrashTitle == null)
            msgCmdTrashTitle = Utils.colorize(config.getString("messages.commands.trash.container-title", "&cTrash bin"));

        return msgCmdTrashTitle;
    }

    public static String getMsgCmdReloaded() {
        if (msgCmdReloaded == null)
            msgCmdReloaded = Utils.colorize(config.getString("messages.commands.reloaded", "&aMyDrops configuration has been reloaded successfully!"));

        return msgCmdReloaded;
    }

    public static String getMsgCmdUnknownSub() {
        return config.getString("messages.commands.unknown", "&cUnknown subcommand &4&o{subcommand}");
    }

    public static String getMsgCmdNoPermission() {
        return config.getString("messages.commands.no-permission", "&cYou do not have permission to execute subcommand &4&o{subcommand}");
    }

    public static String getMsgCmdGlowSet() {
        return config.getString("messages.commands.glow.set", "&eGlow color set to &o{color}");
    }

    public static String getMsgCmdGlowInvalid() {
        return config.getString("messages.commands.glow.invalid", "&eGlow color &o&6{color}&e is not valid");
    }

    public static String getMsgSelectedSuffix() {
        return config.getString("messages.selected-suffix", " (selected)");
    }

    public static String getMsgDefault() {
        if (msgDefault == null)
            msgDefault = Utils.colorize(config.getString("messages.default", "&b&oDefault value"));

        return msgDefault;
    }

    public static String getMsgCmdTrustUsage() {
        if (msgCmdTrustUsage == null)
            msgCmdTrustUsage = Utils.colorize(config.getString("messages.commands.trust.usage", "&cUsage: /{cmd} trust <add/remove/list> [player]"));

        return msgCmdTrustUsage;
    }

    public static String getMsgCmdProtectionUsage(String cmd) {
        if (msgCmdProtectionUsage == null)
            msgCmdProtectionUsage = Utils.colorize(config.getString("messages.commands.protection.usage", "&cUsage: /{cmd} protection <protection type/list> [true/false]"));

        return msgCmdProtectionUsage.replace("{cmd}", cmd);
    }

    public static String getMsgCmdAddRemoveUsage() {
        if (msgCmdAddRemoveUsage == null)
            msgCmdAddRemoveUsage = Utils.colorize(config.getString("messages.commands.trust.add-remove-usage", "&cUsage: /{cmd} trust {subcmd} <player>"));

        return msgCmdAddRemoveUsage;
    }

    public static String getMsgCmdAddSuccess() {
        if (msgCmdAddSuccess == null)
            msgCmdAddSuccess = Utils.colorize(config.getString("messages.commands.trust.add-success", "&2{player}&a has been added to your trusted list"));

        return msgCmdAddSuccess;
    }

    public static String getMsgCmdAddSuccessParty() {
        if (msgCmdAddSuccessParty == null)
            msgCmdAddSuccessParty = Utils.colorize(config.getString("messages.commands.trust.add-success-party", "&2{party}&a has been added to your trusted list"));

        return msgCmdAddSuccessParty;
    }

    public static String getMsgCmdRemoveSuccess() {
        if (msgCmdRemoveSuccess == null)
            msgCmdRemoveSuccess = Utils.colorize(config.getString("messages.commands.trust.remove-success", "&2{player}&a has been removed from your trusted list"));

        return msgCmdRemoveSuccess;
    }

    public static String getMsgCmdRemoveSuccessParty() {
        if (msgCmdRemoveSuccessParty == null)
            msgCmdRemoveSuccessParty = Utils.colorize(config.getString("messages.commands.trust.remove-success-party", "&2{party}&a has been removed from your trusted list"));

        return msgCmdRemoveSuccessParty;
    }

    public static String getMsgCmdTrustAlreadyTrusted() {
        if (msgCmdTrustAlreadyTrusted == null)
            msgCmdTrustAlreadyTrusted = Utils.colorize(config.getString("messages.commands.trust.already-trusted", "&4{player}&c is already part of your trusted list"));

        return msgCmdTrustAlreadyTrusted;
    }

    public static String getMsgCmdTrustNotTrusted() {
        if (msgCmdTrustNotTrusted == null)
            msgCmdTrustNotTrusted = Utils.colorize(config.getString("messages.commands.trust.not-trusted", "&4{player}&c is not part of your trusted list"));

        return msgCmdTrustNotTrusted;
    }

    public static String getMsgCmdTrustNotTrustedParty() {
        if (msgCmdTrustNotTrustedParty == null)
            msgCmdTrustNotTrustedParty = Utils.colorize(config.getString("messages.commands.trust.not-trusted-party", "&4{party}&c is not part of your trusted list"));

        return msgCmdTrustNotTrustedParty;
    }

    public static String getMsgCmdTrustList() {
        if (msgCmdTrustList == null)
            msgCmdTrustList = Utils.colorize(config.getString("messages.commands.trust.list", "&eTrusted players ({count}): &6{players}"));

        return msgCmdTrustList;
    }

    public static String getMsgCmdTrustSelf() {
        if (msgCmdTrustSelf == null)
            msgCmdTrustSelf = Utils.colorize(config.getString("messages.commands.trust.self", "&cYou cannot add yourself to your trusted list"));

        return msgCmdTrustSelf;
    }

    public static boolean hasPerPlayerProtection() {
        return config.getBoolean("options.per-player-protection", false);
    }

    public static boolean isWorldListBlacklist() {
        return config.getBoolean("worlds.is-blacklist", true);
    }

    public static List<String> getWorldList() {
        return config.getStringList("worlds.list");
    }

    public static boolean hasAutoUpdateChecker() {
        return config.getBoolean("options.enable-auto-update-checker", true);
    }

    public static String getTxtConfirmTitle() {
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

        return acceptItem;
    }

    public static ItemStack getDefaultAcceptItem() {
        return defaultAcceptItem;
    }

    public static ItemStack getDeclineItem() {
        if (declineItem == null)
            declineItem = config.getItemStack("items.confirmation.decline", defaultDeclineItem);

        return declineItem;
    }

    public static ItemStack getDefaultDeclineItem() {
        return defaultDeclineItem;
    }

    public static ItemStack getBackItem() {
        if (backItem == null)
            backItem = config.getItemStack("items.gui.back", defaultBackItem);

        return backItem;
    }

    public static ItemStack getDefaultBackItem() {
        return defaultBackItem;
    }

    public static ItemStack getCloseItem() {
        if (closeItem == null)
            closeItem = config.getItemStack("items.gui.close", defaultCloseItem);

        return closeItem;
    }

    public static ItemStack getDefaultCloseItem() {
        return defaultCloseItem;
    }

    public static ItemStack getNoneItem() {
        if (noneItem == null)
            noneItem = config.getItemStack("items.gui.none", defaultNoneItem);

        return noneItem;
    }


    public static void updateValue(String key, Object value) {
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

    public static boolean isConfigurationSection(String path) {
        return config.isConfigurationSection(path);
    }

    public static Object getValue(String key) {
        return config.get(key);
    }

    public static String getDatabaseFormat() {
        return databaseFormat;
    }

    public static String getMsgHelpHeader() {
        if (msgHelpHeader == null)
            msgHelpHeader = Utils.colorize(config.getString("messages.help.header", "&eAll commands are:"));
        return msgHelpHeader;
    }

    public static String getMsgHelpCore() {
        if (msgHelpCore == null)
            msgHelpCore = Utils.colorize(config.getString("messages.help.core", "&6/{cmd}&e - Shows this help menu"));
        return msgHelpCore;
    }

    public static String getMsgHelpGlowcolor() {
        if (msgHelpGlowcolor == null)
            msgHelpGlowcolor = Utils.colorize(config.getString("messages.help.glowcolor", "&6/{cmd} glowcolor [color]&e - Defines a new glowing color for yourself or open the color selection GUI"));
        return msgHelpGlowcolor;
    }

    public static String getMsgHelpProtection() {
        if (msgHelpProtection == null)
            msgHelpProtection = Utils.colorize(config.getString("messages.help.protection", "&6/{cmd} protection <protection type/list> [true/false]&e - Toggles a protection rule for yourself"));
        return msgHelpProtection;
    }

    public static String getMsgHelpProtectionList() {
        if (msgHelpProtectionList == null)
            msgHelpProtectionList = Utils.colorize(config.getString("messages.help.protection.list", "&6/{cmd} protection list&e - List all your protections"));
        return msgHelpProtectionList;
    }

    public static String getMsgHelpTrash() {
        if (msgHelpTrash == null)
            msgHelpTrash = Utils.colorize(config.getString("messages.help.trash", "&6/{cmd} trash &e - Opens up a trash bin container"));
        return msgHelpTrash;
    }

    public static String getMsgHelpTrust() {
        if (msgHelpTrust == null)
            msgHelpTrust = Utils.colorize(config.getString("messages.help.trust", "&6/{cmd} trust <add/addparty/remove/removeparty/list> [player] &e - Controls your trusted list"));
        return msgHelpTrust;
    }

    public static String getMsgHelpConfig() {
        if (msgHelpConfig == null)
            msgHelpConfig = Utils.colorize(config.getString("messages.help.config", "&6/{cmd} config [key] [value] &e - Defines the value of a setting or open the config GUI"));
        return msgHelpConfig;
    }

    public static String getMsgHelpReload() {
        if (msgHelpReload == null)
            msgHelpReload = Utils.colorize(config.getString("messages.help.reload", "&6/{cmd} reload &e - Reloads the configuration from file"));
        return msgHelpReload;
    }

    public static boolean getHideDropsFromOthers() {
        return config.getBoolean("options.hide-drops-from-others", true);
    }

    public static boolean hasHookParties() {
        return config.getBoolean("hooks.parties.enabled", true);
    }

    public static boolean hasHookMythicMobs() {
        return config.getBoolean("hooks.mythicmobs.enabled", true);
    }

    public static boolean doPlayersTrustTheirParty() {
        return config.getBoolean("hooks.parties.players-trust-own-party-by-default", false);
    }

    public static String getMsgYourParty() {
        if (msgYourParty == null)
            msgYourParty = Utils.colorize(config.getString("messages.your-party", "Your party"));

        return msgYourParty;
    }

    public static String getMsgCmdTrustAlreadyTrustedParty() {
        if (msgCmdTrustAlreadyTrustedParty == null)
            msgCmdTrustAlreadyTrustedParty = Utils.colorize(config.getString("messages.commands.trust.already-trusted-party", "&4{party}&c is already part of your trusted list"));
        return msgCmdTrustAlreadyTrustedParty;
    }

    public static String getMsgCmdProtectionInvalidType(String input) {
        if (msgCmdProtectionInvalidType == null)
            msgCmdProtectionInvalidType = Utils.colorize(config.getString("messages.commands.protection.invalid-type", "&cInvalid protection type: &4{type}"));
        return msgCmdProtectionInvalidType.replace("{type}", input);
    }

    public static String getMsgCmdProtectionSet(ProtectionType protectionType, boolean value) {
        if (msgCmdProtectionSet == null)
            msgCmdProtectionSet = Utils.colorize(config.getString("messages.commands.protection.set", "&aProtection type &2{type} &ahas been {status}"));
        return msgCmdProtectionSet.replace("{type}", protectionType.getStringValue()).replace("{status}", value ? msgEnabled : msgDisabled);
    }

    public static String getMsgCmdProtectionList(String formattedProtections) {
        if (msgCmdProtectionList == null)
            msgCmdProtectionList = Utils.colorize(config.getString("messages.commands.protection.list", "&7Your protections: {protections}"));
        return msgCmdProtectionList.replace("{protections}", formattedProtections);
    }
}