package net.maxxqc.mydrops.inventory.gui;

import net.maxxqc.mydrops.inventory.InventoryButton;
import net.maxxqc.mydrops.inventory.InventoryGUI;
import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public class ConfirmationGUI extends InventoryGUI {
    private boolean canClose;

    public ConfirmationGUI() {
        super(true);
        this.canClose = ConfigManager.canCloseConfirmWithEscape();
    }

    @Override
    public void decorate(Player player) {
        InventoryButton acceptButton = new InventoryButton().creator(p -> ConfigManager.getAcceptItem()).clickConsumer(e -> {
            canClose = true;
            player.closeInventory();
        }).dragConsumer(e -> {
            canClose = true;
            player.closeInventory();
        });
        InventoryButton declineButton = new InventoryButton().creator(p -> ConfigManager.getDeclineItem()).clickConsumer(e -> {
            canClose = true;
            Utils.getGuiManager().openGUI(new TrashGUI(player), player);
        }).dragConsumer(e -> {
            canClose = true;
            Utils.getGuiManager().openGUI(new TrashGUI(player), player);
        });

        this.addButton(2, acceptButton);
        this.addButton(6, declineButton);

        super.decorate(player);
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 9, ConfigManager.getTxtConfirmTitle());
    }

    @Override
    public void onClose(InventoryCloseEvent e) {
        if (canClose) {
            Utils.clearTrashContent((Player) e.getPlayer());
            return;
        }

        // Prevent inventory close
        new BukkitRunnable() {
            @Override
            public void run() {
                Utils.getGuiManager().openGUI(new ConfirmationGUI(), (Player) e.getPlayer());
            }
        }.runTaskLater(Utils.plugin, 1L);
    }
}
