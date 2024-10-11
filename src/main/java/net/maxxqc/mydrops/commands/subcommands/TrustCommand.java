package net.maxxqc.mydrops.commands.subcommands;

import com.alessiodp.parties.api.interfaces.Party;
import net.maxxqc.mydrops.commands.CommandInterface;
import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TrustCommand implements CommandInterface
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        if (args.length < 2) {
            sender.sendMessage(ConfigManager.getMsgCmdTrustUsage().replace("{cmd}", commandLabel));
            return true;
        }

        Player source = (Player) sender;

        if (args[1].equalsIgnoreCase("add")) {
            if (args.length < 3) {
                sender.sendMessage(ConfigManager.getMsgCmdAddRemoveUsage().replace("{cmd}", commandLabel).replace("{subcmd}", args[1]));
                return true;
            }

            addPlayer(source, args[2]);
        } else if (args[1].equalsIgnoreCase("addparty")) {
            if (!sender.hasPermission("mydrops.command.trust.party")) {
                sender.sendMessage(Utils.colorize(ConfigManager.getMsgCmdNoPermission().replace("{subcommand}", args[1].toLowerCase())));
                return true;
            }

            addParty(source);
        } else if (args[1].equalsIgnoreCase("remove")) {
            if (args.length < 3) {
                sender.sendMessage(ConfigManager.getMsgCmdAddRemoveUsage().replace("{cmd}", commandLabel).replace("{subcmd}", args[1]));
                return true;
            }

            removePlayer(source, args[2]);
        } else if (args[1].equalsIgnoreCase("removeparty")) {
            if (!sender.hasPermission("mydrops.command.trust.party")) {
                sender.sendMessage(Utils.colorize(ConfigManager.getMsgCmdNoPermission().replace("{subcommand}", args[1].toLowerCase())));
                return true;
            }

            removeParty(source);
        } else {
            listPlayers(source);
        }

        return true;
    }

    private void addPlayer(Player source, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            source.sendMessage(ConfigManager.getMsgPlayerNotFound().replace("{player}", targetName));
            return;
        }

        if (source.getUniqueId().equals(target.getUniqueId())) {
            source.sendMessage(ConfigManager.getMsgCmdTrustSelf());
            return;
        }

        if (ConfigManager.getDatabase().getTrustedPlayers(source).contains(target.getUniqueId().toString())) {
            source.sendMessage(ConfigManager.getMsgCmdTrustAlreadyTrusted().replace("{player}", target.getName()));
            return;
        }

        ConfigManager.getDatabase().addTrustedPlayer(source, target);
        source.sendMessage(ConfigManager.getMsgCmdAddSuccess().replace("{player}", target.getName()));
    }

    private void addParty(Player source) {
        Party party = Utils.getPartyOfPlayer(source);
        String partyName = party == null ? ConfigManager.getMsgYourParty() : party.getName();

        if (ConfigManager.getDatabase().getTrustedParties(source).contains(source.getUniqueId().toString())) {
            source.sendMessage(ConfigManager.getMsgCmdTrustAlreadyTrustedParty().replace("{party}", partyName));
            return;
        }

        ConfigManager.getDatabase().addTrustedParty(source, source.getUniqueId());
        source.sendMessage(ConfigManager.getMsgCmdAddSuccessParty().replace("{party}", partyName));
    }

    private void removeParty(Player source) {
        Party party = Utils.getPartyOfPlayer(source);
        String partyName = party == null ? ConfigManager.getMsgYourParty() : party.getName();

        if (!ConfigManager.getDatabase().getTrustedParties(source).contains(source.getUniqueId().toString())) {
            source.sendMessage(ConfigManager.getMsgCmdTrustNotTrustedParty().replace("{party}", partyName));
            return;
        }

        ConfigManager.getDatabase().removeTrustedParty(source, source.getUniqueId());
        source.sendMessage(ConfigManager.getMsgCmdRemoveSuccessParty().replace("{party}", partyName));
    }

    private void removePlayer(Player source, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            source.sendMessage(ConfigManager.getMsgPlayerNotFound().replace("{player}", targetName));
            return;
        }

        if (!ConfigManager.getDatabase().getTrustedPlayers(source).contains(target.getUniqueId().toString())) {
            source.sendMessage(ConfigManager.getMsgCmdTrustNotTrusted().replace("{player}", target.getName()));
            return;
        }

        ConfigManager.getDatabase().removeTrustedPlayer(source, target);
        source.sendMessage(ConfigManager.getMsgCmdRemoveSuccess().replace("{player}", target.getName()));
    }

    private void listPlayers(Player source) {
        List<String> trustedPlayers = new ArrayList<>();
        for (String uuid : ConfigManager.getDatabase().getTrustedPlayers(source)) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
            if (player != null) {
                trustedPlayers.add(player.getName());
            }
        }

        if (trustedPlayers.isEmpty()) {
            source.sendMessage(ConfigManager.getMsgCmdTrustList().replace("{players}", ConfigManager.getMsgEmpty()).replace("{count}", "0"));
            return;
        }

        source.sendMessage(ConfigManager.getMsgCmdTrustList().replace("{players}", String.join(", ", trustedPlayers)).replace("{count}", String.valueOf(trustedPlayers.size())));
    }

    @Override
    public String getPermission()
    {
        return "mydrops.command.trust";
    }
}