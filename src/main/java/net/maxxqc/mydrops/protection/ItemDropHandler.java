package net.maxxqc.mydrops.protection;

import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.ProtectionType;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class ItemDropHandler implements Listener
{
    @EventHandler
    private void onDrop(PlayerDropItemEvent e)
    {
        if (!ConfigManager.getDatabase().getProtection(e.getPlayer(), ProtectionType.ITEM_DROP))
            return;

        Utils.protectItemDrop(e.getItemDrop(), e.getPlayer());
    }
}