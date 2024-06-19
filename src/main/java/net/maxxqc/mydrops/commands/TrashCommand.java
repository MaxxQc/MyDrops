package net.maxxqc.mydrops.commands;

import net.maxxqc.mydrops.utils.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TrashCommand implements CommandInterface
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = (Player) sender;
        player.openInventory(Bukkit.createInventory(player, 45, ConfigManager.getMsgCmdTrashTitle()));
        return true;
    }

    @Override
    public String getPermission()
    {
        return "mydrops.command.trash";
    }
}