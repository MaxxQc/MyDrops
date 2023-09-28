package net.maxxqc.mydrops.protection;

import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class ItemDropHandler implements Listener
{
    @EventHandler
    private void onDrop(PlayerDropItemEvent e)
    {
        Utils.handleItemDrop(e.getItemDrop(), e.getPlayer());
    }
}