package net.maxxqc.mydrops.protection;

import net.maxxqc.mydrops.inventory.gui.ConfigValueGUI;
import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.Constants;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.UUID;

public class ProtectionHandler implements Listener {
    @EventHandler
    private void onItemSpawn(ItemSpawnEvent e) {
        List<String> canPickupList = Utils.parseItem(e.getEntity());
        if (canPickupList == null)
            return;
        Utils.handleItemDrop(e.getEntity(), Bukkit.getPlayer(UUID.fromString(canPickupList.get(canPickupList.size() - 1))));
    }

    @EventHandler
    private void onPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player) || Utils.canPickup((Player) e.getEntity(), e.getItem()))
            return;

        e.setCancelled(true);
    }

    @EventHandler
    private void onChat(AsyncPlayerChatEvent e) {
        if (Constants.PLAYER_CONFIG_CHAT_MAP.containsKey(e.getPlayer().getUniqueId().toString())) {
            e.setCancelled(true);

            String key = Constants.PLAYER_CONFIG_CHAT_MAP.get(e.getPlayer().getUniqueId().toString());

            if (!e.getMessage().equalsIgnoreCase("cancel")) {
                ConfigManager.updateValue(key, e.getMessage());
            }
            else {
                e.getPlayer().sendMessage(ConfigManager.getMsgCmdConfigInputCancelled());
            }

            Constants.PLAYER_CONFIG_CHAT_MAP.remove(e.getPlayer().getUniqueId().toString());
            Bukkit.getServer().getScheduler().runTask(Utils.plugin, () -> Utils.getGuiManager().openGUI(new ConfigValueGUI(key.split("\\.")[0]), e.getPlayer()));
        }
    }
}