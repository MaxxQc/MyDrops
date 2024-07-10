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
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class ProtectionHandler implements Listener
{
    @EventHandler
    private void onItemSpawn(ItemSpawnEvent e)
    {
        UUID ownerUUID = Utils.parseItem(e.getEntity());
        if (ownerUUID == null) return;
        Utils.handleItemDrop(e.getEntity(), Bukkit.getPlayer(ownerUUID));
    }

    @EventHandler
    private void onPickup(EntityPickupItemEvent e)
    {
        UUID ownerUUID = Utils.parseEntity(e.getItem());
        if (!(e.getEntity() instanceof Player) || ownerUUID == null || e.getEntity().getUniqueId().equals(ownerUUID) || e.getEntity().hasPermission("mydrops.bypass.pickup")) return;
        e.setCancelled(true);
    }

    @EventHandler
    private void onChat(AsyncPlayerChatEvent e) {
        if (Constants.PLAYER_CONFIG_CHAT_MAP.containsKey(e.getPlayer().getUniqueId().toString())) {
            e.setCancelled(true);

            String key = Constants.PLAYER_CONFIG_CHAT_MAP.get(e.getPlayer().getUniqueId().toString());

            if (!e.getMessage().equalsIgnoreCase("cancel")) {
                ConfigManager.updateValue(key, e.getMessage());
            } else {
                e.getPlayer().sendMessage(ConfigManager.getMsgCmdConfigInputCancelled());
            }

            Constants.PLAYER_CONFIG_CHAT_MAP.remove(e.getPlayer().getUniqueId().toString());
            Bukkit.getServer().getScheduler().runTask(Utils.plugin, () -> Utils.getGuiManager().openGUI(new ConfigValueGUI(key.split("\\.")[0]), e.getPlayer()));
        }
    }
}