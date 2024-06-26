package net.maxxqc.mydrops.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class GuiManager implements Listener {
    private final Map<Inventory, InventoryHandler> activeInventories = new HashMap<>();

    public void openGUI(InventoryGUI gui, Player player) {
        registerInventory(gui.getInventory(), gui);
        player.openInventory(gui.getInventory());
    }

    public void registerInventory(Inventory inventory, InventoryHandler handler) {
        if (inventory == null || handler == null) {
            throw new IllegalArgumentException("Inventory and handler must not be null");
        }

        activeInventories.put(inventory, handler);
    }

    public void unregisterInventory(Inventory inventory) {
        activeInventories.remove(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        InventoryHandler handler = activeInventories.get(e.getInventory());
        if (handler != null) {
            handler.onClick(e);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        InventoryHandler handler = activeInventories.get(e.getInventory());
        if (handler != null) {
            handler.onDrag(e);
        }
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent e) {
        InventoryHandler handler = activeInventories.get(e.getInventory());
        if (handler != null) {
            handler.onOpen(e);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Inventory inventory = e.getInventory();
        InventoryHandler handler = activeInventories.get(inventory);
        if (handler != null) {
            handler.onClose(e);
            unregisterInventory(inventory);
        }
    }
}
