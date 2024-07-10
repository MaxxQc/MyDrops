package net.maxxqc.mydrops.commands;

import net.maxxqc.mydrops.inventory.gui.ConfigGUI;
import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ConfigCommand implements CommandInterface
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        if (args.length < 2)
        {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ConfigManager.getMsgCmdPlayerOnly());
                return true;
            }

            Utils.getGuiManager().openGUI(new ConfigGUI(), (Player) sender);
            return true;
        }

        String key = args[1].toLowerCase();

        if (key.startsWith("items.")) {
            if (args.length == 3 && args[2].equalsIgnoreCase("reset")) {
                ItemStack item;

                switch (key) {
                    case "items.confirmation.accept":
                        item = ConfigManager.getDefaultAcceptItem();
                        break;
                    case "items.confirmation.decline":
                        item = ConfigManager.getDefaultDeclineItem();
                        break;
                    case "items.gui.back":
                        item = ConfigManager.getDefaultBackItem();
                        break;
                    case "items.gui.close":
                        item = ConfigManager.getDefaultCloseItem();
                        break;
                    default:
                        item = new ItemStack(Material.COBBLESTONE);
                        break;
                }

                ConfigManager.updateValue(key, item);
                sender.sendMessage(ConfigManager.getMsgCmdConfigSuccess().replace("{key}", key).replace("{value}", item.getType().name()));
                return true;
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage(ConfigManager.getMsgCmdPlayerOnly());
                return true;
            }

            Player player = (Player) sender;
            ItemStack item = player.getInventory().getItemInMainHand();

            if (item.getType().isAir())
            {
                sender.sendMessage(ConfigManager.getMsgCmdInvalidItem());
                return true;
            }

            ConfigManager.updateValue(key, item);
            sender.sendMessage(ConfigManager.getMsgCmdConfigSuccess().replace("{key}", key).replace("{value}", item.getType().name()));

            return true;
        }

        if (args.length == 2)
        {
            sender.sendMessage(ConfigManager.getMsgCmdConfigUsage().replace("{cmd}", commandLabel));
            sender.sendMessage(ConfigManager.getMsgCmdConfigValue().replace("{key}", key).replace("{value}", ConfigManager.getValue(key).toString()));
            return true;
        }

        String value = key.equalsIgnoreCase("options.default-glow-color") ? args[2].toUpperCase() : args[2].toLowerCase();
        List<String> validValues = ConfigManager.CONFIGS_ARGS.get(key);

        if (validValues == null)
        {
            sender.sendMessage(ConfigManager.getMsgCmdConfigInvalidKey().replace("{key}", key));
            return true;
        }

        if (key.equalsIgnoreCase("options.pickup-delay")) {
            try {
                int delay = Integer.parseInt(value);

                if (delay < 0) {
                    sender.sendMessage(ConfigManager.getMsgCmdConfigPosValue().replace("{key}", key).replace("{value}", value));
                    return true;
                }

                ConfigManager.updateValue(key, delay);
                sender.sendMessage(ConfigManager.getMsgCmdConfigSuccess().replace("{key}", key).replace("{value}", value));

                return true;
            } catch (NumberFormatException ex) {
                sender.sendMessage(ConfigManager.getMsgCmdConfigInvalidValue().replace("{key}", key).replace("{value}", value));
                return true;
            }
        } else if (key.equalsIgnoreCase("worlds.list")) {
            boolean added = false;
            List<String> worlds = ConfigManager.getWorldList();
            if (worlds.contains(value)) {
                worlds.remove(value);
            } else {
                added = true;
                worlds.add(value);
            }

            ConfigManager.updateValue(key, worlds);
            sender.sendMessage((added ? ConfigManager.getMsgCmdConfigAdded() : ConfigManager.getMsgCmdConfigRemoved()).replace("{key}", key).replace("{value}", value));

            return true;
        }

        if (key.startsWith("messages.")) {
            value = String.join(" ", args).replaceFirst(args[0] + " " + args[1] + " ", "");
            ConfigManager.updateValue(key, value);
            sender.sendMessage(ConfigManager.getMsgCmdConfigSuccess().replace("{key}", key).replace("{value}", value));
            return true;
        }

        if (!validValues.contains(value))
        {
            sender.sendMessage(ConfigManager.getMsgCmdConfigInvalidValue().replace("{key}", key).replace("{value}", value));
            return true;
        }

        if (value.equals("true") || value.equals("false")) {
            ConfigManager.updateValue(key, Boolean.parseBoolean(value));
        } else {
            ConfigManager.updateValue(key, value);
        }

        sender.sendMessage(ConfigManager.getMsgCmdConfigSuccess().replace("{key}", key).replace("{value}", value));

        return true;
    }

    @Override
    public String getPermission()
    {
        return "mydrops.command.config";
    }
}