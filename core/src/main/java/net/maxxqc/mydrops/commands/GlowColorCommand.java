package net.maxxqc.mydrops.commands;

import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.PlayerDataManager;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GlowColorCommand implements CommandInterface
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = (Player) sender;

        if (!player.hasPermission("mydrops.command.glowcolor"))
        {
            player.sendMessage(Utils.colorize(ConfigManager.getMsgCmdNoPermission().replaceAll("\\{subcommand}", args[0].toLowerCase())));
            return true;
        }

        //Si args == 1 on ouvre le menu si == 2 alors on définit la couleur
        //if (args.length > 2)
        //{
        //    player.sendMessage(ChatColor.RED + "Usage: " + ChatColor.DARK_RED + "/" + commandLabel + " " + args[0] + " [color]");
        //    return true;
        //}

        //TODO commands tab

        if (args.length != 2)
        {
            player.sendMessage(ChatColor.RED + "Usage: " + ChatColor.DARK_RED + "/" + commandLabel + " " + args[0] + " [color]");
            return true;
        }

        if (args[1].equalsIgnoreCase("none"))
        {
            PlayerDataManager.saveGlowColor(player.getUniqueId(), "none");
            player.sendMessage(Utils.colorize(ConfigManager.getMsgCmdGlowSet().replaceAll("\\{color}", "&fnone")));
        }
        else if (args[1].equalsIgnoreCase("default"))
        {
            PlayerDataManager.saveGlowColor(player.getUniqueId(), null);
            player.sendMessage(Utils.colorize(ConfigManager.getMsgCmdGlowSet().replaceAll("\\{color}", ConfigManager.getGlowColor() + ConfigManager.getGlowColor().name().toLowerCase())));
        }
        else
        {
            try
            {
                ChatColor color = ChatColor.valueOf(args[1].toUpperCase());
                PlayerDataManager.saveGlowColor(player.getUniqueId(), color.name());
                player.sendMessage(Utils.colorize(ConfigManager.getMsgCmdGlowSet().replaceAll("\\{color}", color + color.name().toLowerCase())));
            }
            catch (Exception e)
            {
                player.sendMessage(Utils.colorize(ConfigManager.getMsgCmdGlowInvalid().replaceAll("\\{color}", args[1].toLowerCase())));
            }
        }

        return true;
    }
}