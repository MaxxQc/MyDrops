package net.maxxqc.mydrops.protection;

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.ProtectionType;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MythicMobsHandler implements Listener {
    @EventHandler
    public void onMythicMobsDeath(MythicMobDeathEvent e) {
        if (e.getKiller() == null) return;

        Player killer = (Player) e.getKiller();

        if (!ConfigManager.getDatabase().getProtection(killer, ProtectionType.MYTHIC_MOBS))
            return;

        e.getDrops().forEach(is -> Utils.setItemStackOwner(is, killer));
    }
}
