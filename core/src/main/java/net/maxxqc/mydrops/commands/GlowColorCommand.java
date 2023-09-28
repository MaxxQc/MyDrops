package net.maxxqc.mydrops.commands;

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

        //Si args == 1 on ouvre le menu si == 2 alors on définit la couleur
        if (args.length > 2)
        {
            player.sendMessage(ChatColor.RED + "Usage: " + ChatColor.DARK_RED + "/" + commandLabel + " " + args[0] + " [color]");
            return true;
        }

        player.sendMessage("Chosen color: " + args[1]);

        return true;
    }
}