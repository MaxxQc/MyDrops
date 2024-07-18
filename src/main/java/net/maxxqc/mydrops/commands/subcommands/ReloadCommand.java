package net.maxxqc.mydrops.commands.subcommands;

import net.maxxqc.mydrops.commands.CommandInterface;
import net.maxxqc.mydrops.utils.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandInterface
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        ConfigManager.reload();
        sender.sendMessage(ConfigManager.getMsgCmdReloaded());
        return true;
    }

    @Override
    public String getPermission()
    {
        return "mydrops.command.reload";
    }
}