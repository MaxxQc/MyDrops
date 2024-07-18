package net.maxxqc.mydrops.protection;

import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathHandler implements Listener
{
    @EventHandler
    private void onDeath(PlayerDeathEvent e)
    {
        e.getDrops().forEach(is -> Utils.setItemStackOwner(is, e.getEntity()));
    }
}