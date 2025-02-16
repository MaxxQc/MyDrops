package net.maxxqc.mydrops.commands.subcommands;

import net.maxxqc.mydrops.commands.CommandInterface;
import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.ProtectionType;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ProtectionCommand implements CommandInterface
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player player = (Player) sender;

        if (args.length < 2) {
            // TODO open gui
            sender.sendMessage(ConfigManager.getMsgCmdProtectionUsage(commandLabel));
            return true;
        }

        String upperCaseInput = args[1].toUpperCase();
        ProtectionType protectionType;

        try {
            protectionType = ProtectionType.valueOf(upperCaseInput.replace('-', '_'));
        } catch (IllegalArgumentException e) {
            player.sendMessage(ConfigManager.getMsgCmdProtectionInvalidType(args[1]));
            return true;
        }

        if (args.length == 2) {
            boolean newValue = !ConfigManager.getDatabase().getProtection(player, protectionType);
            ConfigManager.getDatabase().setProtection(player, protectionType, newValue);
            player.sendMessage(Utils.colorize(ConfigManager.getMsgCmdProtectionSet(protectionType, newValue)));
            return true;
        }

        String input = args[2].toLowerCase();
        boolean newValue;

        if (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
            newValue = Boolean.parseBoolean(input);
        } else {
            player.sendMessage(ConfigManager.getMsgCmdProtectionInvalidValue(protectionType, input));
            return true;
        }

        ConfigManager.getDatabase().setProtection(player, protectionType, newValue);
        player.sendMessage(Utils.colorize(ConfigManager.getMsgCmdProtectionSet(protectionType, newValue)));

        return true;
    }

    @Override
    public String getPermission()
    {
        return "mydrops.command.protection";
    }
}