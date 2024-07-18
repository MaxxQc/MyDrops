package net.maxxqc.mydrops.inventory.gui;

import net.maxxqc.mydrops.inventory.InventoryButton;
import net.maxxqc.mydrops.inventory.InventoryGUI;
import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.Constants;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ColorSelectGUI extends InventoryGUI {
    private final String KEY;
    private final String SELECTED;
    private final boolean ADD_RESET;
    private final ChatColor DEFAULT_VALUE;
    private final Consumer<ChatColor> CONSUMER;
    private final int SIZE;

    public ColorSelectGUI(String key, String selected, ChatColor defaultValue, boolean addReset, Consumer<ChatColor> consumer) {
        super(true, Utils.nextDivisibleByNine(Constants.ALL_COLORS.size() + (addReset ? 2 : 1)), ConfigManager.getTxtColorGUITitle());
        this.KEY = key;
        this.SELECTED = selected;
        this.DEFAULT_VALUE = defaultValue;
        this.ADD_RESET = addReset;
        this.CONSUMER = consumer;
        this.SIZE = Utils.nextDivisibleByNine(Constants.ALL_COLORS.size() + (addReset ? 2 : 1));
    }

    @Override
    public void decorate(Player player) {
        int i = 0;

        for (Map.Entry<String, Material> entry : Constants.ALL_COLORS.entrySet()) {
            ItemStack is = Utils.getColoredWool(entry.getKey(), entry.getKey().equalsIgnoreCase(this.SELECTED) ? ConfigManager.getMsgSelectedSuffix() : "");

            if (entry.getKey().equalsIgnoreCase(this.SELECTED)) {
                ItemMeta im = is.getItemMeta();
                im.addEnchant(Enchantment.DAMAGE_UNDEAD, 1, true);
                im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                is.setItemMeta(im);
            }

            if (entry.getKey().equalsIgnoreCase(this.DEFAULT_VALUE.name())) {
                ItemMeta im = is.getItemMeta();
                List<String> lore = im.getLore() == null ? new ArrayList<>() : new ArrayList<>(im.getLore());
                lore.add(ConfigManager.getMsgDefault());
                im.setLore(lore);
                is.setItemMeta(im);
            }

            InventoryButton button = new InventoryButton().creator(p -> is).clickConsumer(e -> this.CONSUMER.accept(ChatColor.valueOf(entry.getKey()))).dragConsumer(e -> this.CONSUMER.accept(ChatColor.valueOf(entry.getKey())));
            this.addButton(i, button);
            i++;
        }

        if (this.ADD_RESET) {
            InventoryButton resetButton = new InventoryButton().creator(p -> ConfigManager.getNoneItem()).clickConsumer(e -> this.CONSUMER.accept(ChatColor.RESET)).dragConsumer(e -> this.CONSUMER.accept(ChatColor.RESET));
            this.addButton(i, resetButton);
        }

        InventoryButton backButton = new InventoryButton().creator(p -> KEY.isEmpty() ? ConfigManager.getCloseItem() : ConfigManager.getBackItem()).clickConsumer(e -> {
            if (KEY.isEmpty()) {
                Utils.delayCloseInv(player);
            }
            else {
                Utils.getGuiManager().openGUI(new ConfigValueGUI(KEY), player);
            }
        }).dragConsumer(e -> Utils.getGuiManager().openGUI(new ConfigValueGUI(KEY), player));
        this.addButton(SIZE - 1, backButton);

        super.decorate(player);
    }

    @Override
    public void onClose(InventoryCloseEvent e) {

    }
}
