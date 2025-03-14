package net.maxxqc.mydrops.protection;

import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.ProtectionType;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.UUID;

public class ItemFrameDropHandler implements Listener
{
    @EventHandler
    private void onEntityDmgByEntity(EntityDamageByEntityEvent e)
    {
        if (!(e.getEntity() instanceof ItemFrame ifr))
            return;

        Player owner;

        if (e.getDamager() instanceof Player)
            owner = (Player) e.getDamager();
        else if (e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player)
            owner = (Player) ((Projectile) e.getDamager()).getShooter();
        else
            return;

        if (!ConfigManager.getDatabase().getProtection(owner, ProtectionType.ITEM_FRAME_DROP))
            return;

        ifr.setItem(Utils.setItemStackOwner(ifr.getItem(), owner, true), false);
    }
}