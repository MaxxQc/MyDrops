package net.maxxqc.mydrops.commands;

import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        List<String> completions = new ArrayList<>();

        if (args.length == 1 && sender.hasPermission("mydrops.command.glowcolor"))
            StringUtil.copyPartialMatches(args[0], Arrays.asList("glowcolor"), completions);

        if (args.length == 2 && args[0].equalsIgnoreCase("glowcolor") && sender.hasPermission("mydrops.command.glowcolor"))
            StringUtil.copyPartialMatches(args[1], ALL_COLORS, completions);

        return completions;
    }
}