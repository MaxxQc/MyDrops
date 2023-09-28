package net.maxxqc.mydrops.protection;

import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class HangingBreakHandler implements Listener
{
    @EventHandler
    private void onHangingBreak(HangingBreakByEntityEvent e)
    {
        if (!(e.getRemover() instanceof Player) || ((Player) e.getRemover()).getGameMode() == GameMode.CREATIVE)
            return;

        ItemStack is = null;

        if (e.getEntity() instanceof GlowItemFrame)
            is = new ItemStack(Material.GLOW_ITEM_FRAME);
        else if (e.getEntity() instanceof ItemFrame)
            is = new ItemStack(Material.ITEM_FRAME);
        else if (e.getEntity() instanceof LeashHitch)
            is = new ItemStack(Material.LEAD);
        else if (e.getEntity() instanceof Painting)
            is = new ItemStack(Material.PAINTING);

        if (is == null) return;

        if (e.getEntity() instanceof LeashHitch) return; //TODO remove when leash bug is fixed

        e.setCancelled(true);
        e.getEntity().remove();
        Utils.setItemStackOwner(is, e.getRemover().getUniqueId());
        e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), is);
    }
}