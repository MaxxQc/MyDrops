package net.maxxqc.mydrops.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class InventoryGUI implements InventoryHandler {
    private final Inventory INVENTORY;
    private final Map<Integer, InventoryButton> BUTTONS = new HashMap<>();
    private final boolean HANDLE_CLICKS;

    public InventoryGUI(boolean handleClicks) {
        this.INVENTORY = createInventory();
        this.HANDLE_CLICKS = handleClicks;
    }

    public Inventory getInventory() {
        return this.INVENTORY;
    }

    public void addButton(int slot, InventoryButton button) {
        this.BUTTONS.put(slot, button);
    }

    public void decorate(Player player) {
        this.BUTTONS.forEach((slot, button) -> {
            ItemStack icon = button.getIconCreator().apply(player);
            this.INVENTORY.setItem(slot, icon);
        });
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        if (!HANDLE_CLICKS) return;

        e.setCancelled(true);
        int slot = e.getRawSlot();
        InventoryButton button = this.BUTTONS.get(slot);
        if (button != null) {
            button.getClickConsumer().accept(e);
        }
    }

    @Override
    public void onDrag(InventoryDragEvent e) {
        if (!HANDLE_CLICKS) return;

        e.setCancelled(true);
        int slot = e.getRawSlots().iterator().next();
        InventoryButton button = this.BUTTONS.get(slot);
        if (button != null) {
            button.getDragConsumer().accept(e);
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent e) {
        this.decorate((Player) e.getPlayer());
    }

    protected abstract Inventory createInventory();
}
