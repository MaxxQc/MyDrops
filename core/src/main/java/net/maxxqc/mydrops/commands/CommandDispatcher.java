package net.maxxqc.mydrops.commands;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import net.maxxqc.mydrops.utils.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandDispatcher implements CommandExecutor
{
    private static final HashMap<String, CommandInterface> COMMANDS = new HashMap<>();

    public void register(String name, CommandInterface cmd)
    {
        COMMANDS.put(name, cmd);
    }

    public boolean exists(String name)
    {
        return COMMANDS.containsKey(name);
    }

    public CommandInterface getExecutor(String name)
    {
        return COMMANDS.get(name);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage(ConfigManager.getMsgCmdPlayerOnly());
            return true;
        }

        if (args.length == 0) {
            getExecutor("mydrops").onCommand(sender, command, label, args);
            return true;
        }

        if (exists(args[0])) {
            getExecutor(args[0]).onCommand(sender, command, label, args);
            return true;
        } else {
            sender.sendMessage(IridiumColorAPI.process(ConfigManager.getMsgCmdUnknownSub().replaceAll("\\{subcommand}", args[0].toLowerCase())));
            return true;
        }
    }
}