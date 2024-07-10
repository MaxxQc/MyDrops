package net.maxxqc.mydrops.inventory.gui;

import net.maxxqc.mydrops.inventory.InventoryButton;
import net.maxxqc.mydrops.inventory.InventoryGUI;
import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.Constants;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Map;
import java.util.function.Consumer;

public class ColorSelectGUI extends InventoryGUI {
    private final String KEY;
    private final Consumer<ChatColor> CONSUMER;
    private final int SIZE;

    public ColorSelectGUI(String key, Consumer<ChatColor> consumer) {
        super(true, Utils.nextDivisibleByNine(Constants.ALL_COLORS.size() + 2), ConfigManager.getTxtColorGUITitle());
        this.KEY = key;
        this.CONSUMER = consumer;
        this.SIZE = Utils.nextDivisibleByNine(Constants.ALL_COLORS.size() + 2);
    }

    @Override
    public void decorate(Player player) {
        int i = 0;

        for (Map.Entry<String, Material> entry : Constants.ALL_COLORS.entrySet()) {
            InventoryButton button = new InventoryButton().creator(p -> Utils.getColoredWool(entry.getKey())).clickConsumer(e -> this.CONSUMER.accept(ChatColor.valueOf(entry.getKey()))).dragConsumer(e -> this.CONSUMER.accept(ChatColor.valueOf(entry.getKey())));
            this.addButton(i, button);
            i++;
        }

        InventoryButton backButton = new InventoryButton().creator(p -> ConfigManager.getBackItem()).clickConsumer(e -> Utils.getGuiManager().openGUI(new ConfigValueGUI(KEY), player)).dragConsumer(e -> Utils.getGuiManager().openGUI(new ConfigValueGUI(KEY), player));
        this.addButton(SIZE - 1, backButton);

        super.decorate(player);
    }

    @Override
    public void onClose(InventoryCloseEvent e) {

    }
}
