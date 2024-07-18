package net.maxxqc.mydrops.commands.subcommands;

import net.maxxqc.mydrops.commands.CommandInterface;
import net.maxxqc.mydrops.inventory.gui.TrashGUI;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TrashCommand implements CommandInterface
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = (Player) sender;
        Utils.getGuiManager().openGUI(new TrashGUI(player), player);
        return true;
    }

    @Override
    public String getPermission()
    {
        return "mydrops.command.trash";
    }
}