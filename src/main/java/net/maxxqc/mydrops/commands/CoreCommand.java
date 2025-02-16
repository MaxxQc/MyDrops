package net.maxxqc.mydrops.commands;

import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.Constants;
import net.maxxqc.mydrops.utils.ProtectionType;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class CoreCommand implements CommandInterface, TabCompleter {
    private final List<String> ALL_COLORS;
    private final Set<String> PROTECTION_ARGS;

    public CoreCommand() {
        ALL_COLORS = new ArrayList<>(Constants.ALL_COLORS.keySet());
        ALL_COLORS.add("NONE");
        ALL_COLORS.add("DEFAULT");

        PROTECTION_ARGS = Arrays.stream(ProtectionType.values())
                .filter(ConfigManager::hasServerProtection)
                .map(ProtectionType::getStringValue)
                .collect(Collectors.toSet());

        PROTECTION_ARGS.add("list");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player player = (Player) sender;

        player.sendMessage(ConfigManager.getMsgHelpHeader());
        player.sendMessage(ConfigManager.getMsgHelpCore().replace("{cmd}", commandLabel));

        if (ConfigManager.hasPerPlayerGlow() && player.hasPermission("mydrops.command.glowcolor"))
            player.sendMessage(ConfigManager.getMsgHelpGlowcolor().replace("{cmd}", commandLabel));

        if (ConfigManager.hasPerPlayerProtection() && player.hasPermission("mydrops.command.protection"))
            player.sendMessage(ConfigManager.getMsgHelpProtection().replace("{cmd}", commandLabel));

        if (player.hasPermission("mydrops.command.trash"))
            player.sendMessage(ConfigManager.getMsgHelpTrash().replace("{cmd}", commandLabel));

        if (player.hasPermission("mydrops.command.trust"))
            player.sendMessage(ConfigManager.getMsgHelpTrust().replace("{cmd}", commandLabel));

        if (player.hasPermission("mydrops.command.config"))
            player.sendMessage(ConfigManager.getMsgHelpConfig().replace("{cmd}", commandLabel));

        if (player.hasPermission("mydrops.command.reload"))
            player.sendMessage(ConfigManager.getMsgHelpReload().replace("{cmd}", commandLabel));

        return true;
    }

    @Override
    public String getPermission() {
        return "mydrops.command.core";
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("mydrops.command.glowcolor"))
                StringUtil.copyPartialMatches(args[0], Collections.singletonList("glowcolor"), completions);

            if (sender.hasPermission("mydrops.command.protection"))
                StringUtil.copyPartialMatches(args[0], Collections.singletonList("protection"), completions);

            if (sender.hasPermission("mydrops.command.trash"))
                StringUtil.copyPartialMatches(args[0], Collections.singletonList("trash"), completions);

            if (sender.hasPermission("mydrops.command.reload")) {
                StringUtil.copyPartialMatches(args[0], Collections.singletonList("reload"), completions);
                StringUtil.copyPartialMatches(args[0], Collections.singletonList("rl"), completions);
            }

            if (sender.hasPermission("mydrops.command.trust"))
                StringUtil.copyPartialMatches(args[0], Collections.singletonList("trust"), completions);

            if (sender.hasPermission("mydrops.command.config"))
                StringUtil.copyPartialMatches(args[0], Collections.singletonList("config"), completions);
        }
        else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("glowcolor") && sender.hasPermission("mydrops.command.glowcolor")) {
                StringUtil.copyPartialMatches(args[1], ALL_COLORS, completions);
            }
            else if (args[0].equalsIgnoreCase("protection") && sender.hasPermission("mydrops.command.protection")) {
                StringUtil.copyPartialMatches(args[1], PROTECTION_ARGS, completions);
            }
            else if (args[0].equalsIgnoreCase("config") && sender.hasPermission("mydrops.command.config")) {
                StringUtil.copyPartialMatches(args[1], ConfigManager.CONFIGS_ARGS.keySet(), completions);
            }
            else if (args[0].equalsIgnoreCase("trust") && sender.hasPermission("mydrops.command.trust")) {
                StringUtil.copyPartialMatches(args[1],
                        Utils.isPartiesHooked() && sender.hasPermission("mydrops.command.trust.party") ?
                                Arrays.asList("add", "addparty", "remove", "removeparty", "list") :
                                Arrays.asList("add", "remove", "list")
                        , completions);
            }
        }
        //TODO protection only return enabled protection on server
        else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("config") && sender.hasPermission("mydrops.command.config")) {
                if (args[1].equalsIgnoreCase("worlds.list")) {
                    Set<String> set = Bukkit.getWorlds().stream().map(WorldInfo::getName).collect(Collectors.toSet());
                    set.addAll(ConfigManager.getWorldList());
                    StringUtil.copyPartialMatches(args[2], set, completions);
                }
                else {
                    StringUtil.copyPartialMatches(args[2], ConfigManager.CONFIGS_ARGS.getOrDefault(args[1].toLowerCase(), Collections.emptyList()), completions);
                }
            }
            else if (args[0].equalsIgnoreCase("protection") && sender.hasPermission("mydrops.command.protection") && !args[1].equalsIgnoreCase("list")) {
                StringUtil.copyPartialMatches(args[2], Arrays.asList("true", "false"), completions);
            }
            else if (args[0].equalsIgnoreCase("trust") && sender.hasPermission("mydrops.command.trust") && (sender instanceof Player)) {
                if (args[1].equalsIgnoreCase("add")) {
                    return StringUtil.copyPartialMatches(args[2], Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(name -> !name.equals(sender.getName())).collect(Collectors.toList()), completions);
                }
                else if (args[1].equalsIgnoreCase("remove")) {
                    List<String> trustedPlayers = ConfigManager.getDatabase().getTrustedPlayers((Player) sender);
                    return StringUtil.copyPartialMatches(args[2], trustedPlayers.stream().map(uuid -> Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName()).collect(Collectors.toList()), completions);
                }
            }
        }
        else {
            if (args[0].equalsIgnoreCase("config") && sender.hasPermission("mydrops.command.config") && args[1].startsWith("messages.")) {
                StringUtil.copyPartialMatches(args[args.length - 1], ConfigManager.CONFIGS_ARGS.getOrDefault(args[1].toLowerCase(), Collections.emptyList()), completions);
            }
        }

        return completions;
    }
}