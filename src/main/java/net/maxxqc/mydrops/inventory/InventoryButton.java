package net.maxxqc.mydrops.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

public class InventoryButton {
    private Function<Player, ItemStack> iconCreator;
    private Consumer<InventoryClickEvent> clickConsumer;
    private Consumer<InventoryDragEvent> dragConsumer;

    public InventoryButton creator(Function<Player, ItemStack> iconCreator) {
        this.iconCreator = iconCreator;
        return this;
    }

    public InventoryButton clickConsumer(Consumer<InventoryClickEvent> clickConsumer) {
        this.clickConsumer = clickConsumer;
        return this;
    }

    public InventoryButton dragConsumer(Consumer<InventoryDragEvent> dragConsumer) {
        this.dragConsumer = dragConsumer;
        return this;
    }

    public Consumer<InventoryClickEvent> getClickConsumer() {
        return this.clickConsumer;
    }

    public Consumer<InventoryDragEvent> getDragConsumer() {
        return this.dragConsumer;
    }

    public Function<Player, ItemStack> getIconCreator() {
        return this.iconCreator;
    }
}
