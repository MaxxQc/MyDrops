package net.maxxqc.mydrops.commands;

import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.Constants;
import net.maxxqc.mydrops.utils.ProtectionTypes;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class CoreCommand implements CommandInterface, TabCompleter
{
    private final List<String> ALL_COLORS;
    private final Set<String> PROTECTION_ARGS;

    public CoreCommand()
    {
        ALL_COLORS = new ArrayList<>(Constants.ALL_COLORS.keySet());
        ALL_COLORS.add("NONE");
        ALL_COLORS.add("DEFAULT");

        PROTECTION_ARGS = Arrays.stream(ProtectionTypes.values()).map(Enum::toString).collect(Collectors.toSet());

        if (!ConfigManager.hasItemDropProtection())
            PROTECTION_ARGS.remove("itemdrop");

        if (!ConfigManager.hasBlockBreakProtection())
            PROTECTION_ARGS.remove("blockbreak");

        if (!ConfigManager.hasItemFrameDropProtection())
            PROTECTION_ARGS.remove("itemframedrop");

        if (!ConfigManager.hasVehicleDestroyProtection())
            PROTECTION_ARGS.remove("vehicledestroy");

        if (!ConfigManager.hasHangingBreakProtection())
            PROTECTION_ARGS.remove("hangingbreak");

        if (!ConfigManager.hasEntityKillProtection())
            PROTECTION_ARGS.remove("entitykill");

        if (!ConfigManager.hasPlayerDeathProtection())
            PROTECTION_ARGS.remove("playerdeath");

        if (!ConfigManager.hasMythicMobsProtection())
            PROTECTION_ARGS.remove("mythicmobs");

        PROTECTION_ARGS.add("list");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        // TODO add messages to config
        Player player = (Player) sender;

        player.sendMessage(Utils.colorize("&eAll commands are:"));
        player.sendMessage(Utils.colorize("&6/" + commandLabel + " &e - Shows this help menu"));

        if (ConfigManager.hasPerPlayerGlow() && player.hasPermission("mydrops.command.glowcolor"))
            player.sendMessage(Utils.colorize("&6/" + commandLabel + " glowcolor [color]&e - Defines a new glowing color for yourself"));

        if (ConfigManager.hasPerPlayerProtection() && player.hasPermission("mydrops.command.protection"))
            player.sendMessage(Utils.colorize("&6/" + commandLabel + " protection <protection type> <true/false>&e - Toggles a protection rule for yourself"));

        if (player.hasPermission("mydrops.command.trash"))
            player.sendMessage(Utils.colorize("&6/" + commandLabel + " trash &e - Opens up a trash bin container"));

        if (player.hasPermission("mydrops.command.reload"))
            player.sendMessage(Utils.colorize("&6/" + commandLabel + " reload &e - Reloads the configuration from file"));

        if (player.hasPermission("mydrops.command.config"))
            player.sendMessage(Utils.colorize("&6/" + commandLabel + " config <key> [value] &e - Defines the value of a setting"));

        return true;
    }

    @Override
    public String getPermission()
    {
        return "mydrops.command.core";
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args)
    {
        List<String> completions = new ArrayList<>();

        if (args.length == 1)
        {
            if (sender.hasPermission("mydrops.command.glowcolor"))
                StringUtil.copyPartialMatches(args[0], Collections.singletonList("glowcolor"), completions);

            if (sender.hasPermission("mydrops.command.protection"))
                StringUtil.copyPartialMatches(args[0], Collections.singletonList("protection"), completions);

            if (sender.hasPermission("mydrops.command.trash"))
                StringUtil.copyPartialMatches(args[0], Collections.singletonList("trash"), completions);

            if (sender.hasPermission("mydrops.command.reload")) {
                StringUtil.copyPartialMatches(args[0], Collections.singletonList("reload"), completions);
                StringUtil.copyPartialMatches(args[0], Collections.singletonList("rl"), completions);
            }

            if (sender.hasPermission("mydrops.command.config"))
                StringUtil.copyPartialMatches(args[0], Collections.singletonList("config"), completions);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("glowcolor") && sender.hasPermission("mydrops.command.glowcolor")) {
                StringUtil.copyPartialMatches(args[1], ALL_COLORS, completions);
            } else if (args[0].equalsIgnoreCase("protection") && sender.hasPermission("mydrops.command.protection")) {


                StringUtil.copyPartialMatches(args[1], PROTECTION_ARGS, completions);
            } else if (args[0].equalsIgnoreCase("config") && sender.hasPermission("mydrops.command.config")) {
                StringUtil.copyPartialMatches(args[1], ConfigManager.CONFIGS_ARGS.keySet(), completions);
            }
        }
        //TODO protection only return enabled protection on server
        else if (args.length == 3)
        {
            if (args[0].equalsIgnoreCase("config") && sender.hasPermission("mydrops.command.config"))
            {
                if (args[1].equalsIgnoreCase("worlds.list")) {
                    Set<String> set = Bukkit.getWorlds().stream().map(WorldInfo::getName).collect(Collectors.toSet());
                    set.addAll(ConfigManager.getWorldList());
                    StringUtil.copyPartialMatches(args[2], set, completions);
                } else {
                    StringUtil.copyPartialMatches(args[2], ConfigManager.CONFIGS_ARGS.getOrDefault(args[1].toLowerCase(), Collections.emptyList()), completions);
                }
            }
            else if (args[0].equalsIgnoreCase("protection") && sender.hasPermission("mydrops.command.protection") && !args[1].equalsIgnoreCase("list"))
            {
                StringUtil.copyPartialMatches(args[2], Arrays.asList("true", "false"), completions);
            }
        } else {
            if (args[0].equalsIgnoreCase("config") && sender.hasPermission("mydrops.command.config") && args[1].startsWith("messages.")) {
                StringUtil.copyPartialMatches(args[args.length - 1], ConfigManager.CONFIGS_ARGS.getOrDefault(args[1].toLowerCase(), Collections.emptyList()), completions);
            }
        }

        return completions;
    }
}