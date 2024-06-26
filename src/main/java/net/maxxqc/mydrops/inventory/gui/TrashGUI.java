package net.maxxqc.mydrops.inventory.gui;

import net.maxxqc.mydrops.inventory.InventoryGUI;
import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class TrashGUI extends InventoryGUI {
    public TrashGUI(Player player) {
        super(false);
        if (Utils.hasTrashContent(player)) {
            this.getInventory().setContents(Utils.getTrashContent(player));
        }
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 45, ConfigManager.getMsgCmdTrashTitle());
    }

    @Override
    public void onClose(InventoryCloseEvent e) {
        if (ConfigManager.hasTrashConfirmClose()) {
            boolean used = false;
            for (ItemStack is : getInventory().getContents()) {
                if (is != null && !is.getType().isAir()) {
                    used = true;
                    break;
                }
            }

            if (!used) return;

            Utils.saveTrashContent((Player) e.getPlayer(), e.getInventory().getContents());

            new BukkitRunnable() {
                @Override
                public void run() {
                    Utils.getGuiManager().openGUI(new ConfirmationGUI(), (Player) e.getPlayer());
                }
            }.runTaskLater(Utils.plugin, 1L);
        }
    }
}
