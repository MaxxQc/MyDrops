package net.maxxqc.mydrops.protection;

import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.ProtectionType;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityKillHandler implements Listener
{
    @EventHandler
    private void onKill(EntityDeathEvent e)
    {
        if (e.getEntity().getKiller() == null || !ConfigManager.getDatabase().getProtection(e.getEntity().getKiller(), ProtectionType.ENTITY_KILL))
            return;

        e.getDrops().forEach(is -> Utils.setItemStackOwner(is, e.getEntity().getKiller()));
    }
}