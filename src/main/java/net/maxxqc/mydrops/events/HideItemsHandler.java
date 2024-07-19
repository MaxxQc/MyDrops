package net.maxxqc.mydrops.events;

import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class HideItemsHandler implements Listener {
    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        for (Entity item : Bukkit.getServer().selectEntities(e.getPlayer(), "@e[type=item]")) {
            if (Utils.canPickup(e.getPlayer(), (Item) item))
                continue;

            e.getPlayer().hideEntity(Utils.plugin, item);
        }
    }
}
