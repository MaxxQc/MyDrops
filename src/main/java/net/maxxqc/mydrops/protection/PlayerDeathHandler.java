package net.maxxqc.mydrops.protection;

import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.ProtectionType;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathHandler implements Listener
{
    @EventHandler
    private void onDeath(PlayerDeathEvent e)
    {
        if (!ConfigManager.getDatabase().getProtection(e.getEntity(), ProtectionType.PLAYER_DEATH))
            return;

        e.getDrops().forEach(is -> Utils.setItemStackOwner(is, e.getEntity()));
    }
}