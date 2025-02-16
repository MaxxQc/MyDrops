package net.maxxqc.mydrops.protection;

import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.ProtectionType;
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
        if (!ConfigManager.getDatabase().getProtection(e.getPlayer(), ProtectionType.BLOCK_BREAK))
            return;

        e.getItems().forEach(i -> Utils.protectItemDrop(i, e.getPlayer()));
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent e)
    {
        if (!ConfigManager.getDatabase().getProtection(e.getPlayer(), ProtectionType.BLOCK_BREAK))
            return;

        if (e.getBlock().getState() instanceof Container c)
        {
            for (ItemStack is : c.getInventory().getContents())
            {
                if (is == null)
                    continue;

                Utils.setItemStackOwner(is, e.getPlayer());
            }
        }
        else {
            Utils.setBlockOwner(e.getBlock(), e.getPlayer().getUniqueId());
        }
    }
}