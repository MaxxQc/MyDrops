package net.maxxqc.mydrops.protection;

import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
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
}