package net.maxxqc.mydrops.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface CommandInterface
{
    boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args);

    String getPermission();
}