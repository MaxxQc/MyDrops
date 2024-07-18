package net.maxxqc.mydrops.commands.subcommands;

import net.maxxqc.mydrops.commands.CommandInterface;
import net.maxxqc.mydrops.inventory.gui.ColorSelectGUI;
import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GlowColorCommand implements CommandInterface {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player player = (Player) sender;

        if (args.length < 2) {
            Utils.getGuiManager().openGUI(new ColorSelectGUI("", ConfigManager.getDatabase().getGlowColor(player), ConfigManager.getGlowColor(), true, chatColor -> {
                ConfigManager.getDatabase().setGlowColor(player, chatColor == ChatColor.RESET ? "none" : chatColor.name());
                Utils.delayCloseInv(player);
                player.sendMessage(Utils.colorize(ConfigManager.getMsgCmdGlowSet().replace("{color}", chatColor + (chatColor == ChatColor.RESET ? "none" : chatColor.name().toLowerCase()))));
            }), player);
            return true;
        }

        if (args[1].equalsIgnoreCase("none")) {
            ConfigManager.getDatabase().setGlowColor(player, "none");
            player.sendMessage(Utils.colorize(ConfigManager.getMsgCmdGlowSet().replace("{color}", "&fnone")));
        }
        else if (args[1].equalsIgnoreCase("default")) {
            ConfigManager.getDatabase().setGlowColor(player, (ChatColor) null);
            player.sendMessage(Utils.colorize(ConfigManager.getMsgCmdGlowSet().replace("{color}", ConfigManager.getGlowColor() + ConfigManager.getGlowColor().name().toLowerCase())));
        }
        else {
            try {
                ChatColor color = ChatColor.valueOf(args[1].toUpperCase());

                if (color == ConfigManager.getGlowColor()) {
                    ConfigManager.getDatabase().setGlowColor(player, (ChatColor) null);
                }
                else {
                    ConfigManager.getDatabase().setGlowColor(player, color);
                }

                player.sendMessage(Utils.colorize(ConfigManager.getMsgCmdGlowSet().replace("{color}", color + color.name().toLowerCase())));
            }
            catch (Exception e) {
                player.sendMessage(Utils.colorize(ConfigManager.getMsgCmdGlowInvalid().replace("{color}", args[1].toLowerCase())));
            }
        }

        return true;
    }

    @Override
    public String getPermission() {
        return "mydrops.command.glowcolor";
    }
}