package net.maxxqc.mydrops.inventory.gui;

import net.maxxqc.mydrops.inventory.InventoryButton;
import net.maxxqc.mydrops.inventory.InventoryGUI;
import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class ConfigGUI extends InventoryGUI {
    private final int SIZE;

    public ConfigGUI() {
        super(true, Utils.nextDivisibleByNine(ConfigManager.getGlobalKeys().size() + 2), ConfigManager.getTxtConfigGUITitle().replace("{key}", "Global"));

        this.SIZE = Utils.nextDivisibleByNine(ConfigManager.getGlobalKeys().size() + 2);
    }

    @Override
    public void decorate(Player player) {
        int i = 0;
        for (String key : ConfigManager.getGlobalKeys()) {
            if (key.equalsIgnoreCase("database"))
                continue;

            ItemStack is = Utils.createItemStack(Material.WHITE_WOOL, "&b" + key);
            InventoryButton button = new InventoryButton().creator(p -> is).clickConsumer(e -> Utils.getGuiManager().openGUI(new ConfigValueGUI(key), player)).dragConsumer(e -> Utils.getGuiManager().openGUI(new ConfigValueGUI(key), player));
            this.addButton(i, button);

            i++;
        }

        InventoryButton backButton = new InventoryButton().creator(p -> ConfigManager.getCloseItem()).clickConsumer(e -> Utils.delayCloseInv(player)).dragConsumer(e -> player.closeInventory());
        this.addButton(SIZE - 1, backButton);

        super.decorate(player);
    }

    @Override
    public void onClose(InventoryCloseEvent e) {

    }
}
