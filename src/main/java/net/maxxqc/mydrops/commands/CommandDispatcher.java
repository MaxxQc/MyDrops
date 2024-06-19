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

        if (args.length == 0)
        {
            CommandInterface interf = getExecutor("mydrops");
            if (sender.hasPermission(interf.getPermission()))
            {
                interf.onCommand(sender, command, label, args);
            }
            else
            {
                sender.sendMessage(IridiumColorAPI.process(ConfigManager.getMsgCmdNoPermission().replaceAll("\\{subcommand}", "mydrops")));
            }

            return true;
        }

        if (exists(args[0]))
        {
            CommandInterface inter = getExecutor(args[0]);

            if (sender.hasPermission(inter.getPermission()))
            {
                inter.onCommand(sender, command, label, args);
            }
            else
            {
                sender.sendMessage(IridiumColorAPI.process(ConfigManager.getMsgCmdNoPermission().replaceAll("\\{subcommand}", args[0].toLowerCase())));
            }

            return true;
        }
        else
        {
            sender.sendMessage(IridiumColorAPI.process(ConfigManager.getMsgCmdUnknownSub().replaceAll("\\{subcommand}", args[0].toLowerCase())));
            return true;
        }
    }
}