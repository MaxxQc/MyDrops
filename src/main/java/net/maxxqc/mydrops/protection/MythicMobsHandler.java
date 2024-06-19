package net.maxxqc.mydrops.protection;

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MythicMobsHandler implements Listener {
    @EventHandler
    public void onMythicMobsDeath(MythicMobDeathEvent e) {
        if (e.getKiller() == null) return;

        e.getDrops().forEach(is -> Utils.setItemStackOwner(is, e.getKiller().getUniqueId()));
    }
}
