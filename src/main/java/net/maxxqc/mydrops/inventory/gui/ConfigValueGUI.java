package net.maxxqc.mydrops.inventory.gui;

import net.maxxqc.mydrops.inventory.InventoryButton;
import net.maxxqc.mydrops.inventory.InventoryGUI;
import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class ConfigValueGUI extends InventoryGUI {
    private final String KEY;
    private final int SIZE;

    public ConfigValueGUI(String key) {
        super(true, Utils.nextDivisibleByNine(ConfigManager.getGlobalKeys().size() + 2), ConfigManager.getTxtConfigGUITitle().replace("{key}", key));
        this.KEY = key;
        this.SIZE = Utils.nextDivisibleByNine(ConfigManager.getGlobalKeys().size() + 2);
    }

    @Override
    public void decorate(Player player) {
        int i = 0;
        /*
        for (String key : ConfigManager.getGlobalKeys()) {
            ItemStack is = new ItemStack(Material.WHITE_WOOL);
            ItemMeta im = is.getItemMeta();
            im.setDisplayName(Utils.colorize("&b" + key));
            is.setItemMeta(im);

            InventoryButton button = new InventoryButton().creator(p -> is).clickConsumer(e -> Utils.getGuiManager().openGUI(new ConfigValueGUI(key), player)).dragConsumer(e -> Utils.getGuiManager().openGUI(new ConfigValueGUI(key), player));

            this.addButton(i, button);
            i++;
        }*/

        InventoryButton backButton = new InventoryButton().creator(p -> ConfigManager.getBackItem()).clickConsumer(e -> Utils.getGuiManager().openGUI(new ConfigGUI(), player)).dragConsumer(e -> Utils.getGuiManager().openGUI(new ConfigGUI(), player));
        this.addButton(SIZE - 1, backButton);

        super.decorate(player);
    }

    @Override
    public void onClose(InventoryCloseEvent e) {

    }
}
