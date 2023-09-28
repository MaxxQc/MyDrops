package net.maxxqc.mydrops.protection;

import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakHandler implements Listener
{
    @EventHandler
    private void onBlockDrop(BlockDropItemEvent e)
    {
        e.getItems().forEach(i -> Utils.handleItemDrop(i, e.getPlayer()));
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent e)
    {
        if (e.getBlock().getState() instanceof Container)
        {
            Container c = ((Container) e.getBlock().getState());

            for (ItemStack is : c.getInventory().getContents())
            {
                if (is == null)
                    continue;
                Utils.setItemStackOwner(is, e.getPlayer().getUniqueId());
            }
        }
        else
            Utils.setBlockOwner(e.getBlock(), e.getPlayer().getUniqueId());
    }
}