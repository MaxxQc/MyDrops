package net.maxxqc.mydrops.commands;

import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.ProtectionTypes;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

public class CoreCommand implements CommandInterface, TabCompleter
{
    private final List<String> ALL_COLORS;

    public CoreCommand()
    {
        ALL_COLORS = Arrays.stream(ChatColor.values()).map(chatColor -> chatColor.name().toLowerCase()).collect(Collectors.toList());
        ALL_COLORS.add("none");
        ALL_COLORS.add("default");
        ALL_COLORS.remove("reset");
        ALL_COLORS.remove("bold");
        ALL_COLORS.remove("italic");
        ALL_COLORS.remove("strikethrough");
        ALL_COLORS.remove("underline");
        ALL_COLORS.remove("magic");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = (Player) sender;

        player.sendMessage(Utils.colorize("&eAll commands are:"));
        player.sendMessage(Utils.colorize("&6/mydrops &e - Shows this help menu"));

        if (ConfigManager.hasPerPlayerGlow() && player.hasPermission("mydrops.command.glowcolor"))
            player.sendMessage(Utils.colorize("&6/mydrops glowcolor [color]&e - Defines a new glowing color"));

        return true;
    }

    @Override
    public String getPermission()
    {
        return "mydrops.command.core";
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        List<String> completions = new ArrayList<>();

        if (args.length == 1)
        {
            if (sender.hasPermission("mydrops.command.glowcolor"))
                StringUtil.copyPartialMatches(args[0], Collections.singletonList("glowcolor"), completions);

            if (sender.hasPermission("mydrops.command.protection"))
                StringUtil.copyPartialMatches(args[0], Collections.singletonList("protection"), completions);
        }
        else if (args.length == 2)
        {
            if (args[0].equalsIgnoreCase("glowcolor") && sender.hasPermission("mydrops.command.glowcolor"))
                StringUtil.copyPartialMatches(args[1], ALL_COLORS, completions);
            else if (args[0].equalsIgnoreCase("protection") && sender.hasPermission("mydrops.command.protection"))
            {
                List<String> col = Arrays.stream(ProtectionTypes.values()).map(Enum::toString).collect(Collectors.toList());
                col.add("list");
                StringUtil.copyPartialMatches(args[1], col, completions);
            }
        }
        else if (args.length == 3)
        {
            if (args[0].equalsIgnoreCase("protection") && sender.hasPermission("mydrops.command.protection") && !args[1].equalsIgnoreCase("list"))
            {
                StringUtil.copyPartialMatches(args[2], Arrays.asList("true", "false"), completions);
            }
        }

        return completions;
    }
}