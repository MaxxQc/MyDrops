package net.maxxqc.mydrops.inventory.gui;

import net.maxxqc.mydrops.inventory.InventoryButton;
import net.maxxqc.mydrops.inventory.InventoryGUI;
import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.Constants;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.generator.WorldInfo;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigValueGUI extends InventoryGUI {
    private final String KEY;
    private final List<String> KEYS;
    private final int SIZE;

    public ConfigValueGUI(String key) {
        super(true, Utils.nextDivisibleByNine((key.equalsIgnoreCase("worlds") ? (int) Stream.concat(ConfigManager.getWorldList().stream(), Bukkit.getWorlds().stream().map(WorldInfo::getName).toList().stream()).distinct().count() : ConfigManager.getKeys(key).size()) + 2), ConfigManager.getTxtConfigGUITitle().replace("{key}", key));
        this.KEY = key;
        this.KEYS = ConfigManager.getKeys(key);
        this.SIZE = Utils.nextDivisibleByNine(this.KEYS.size() + 2);
    }

    @Override
    public void decorate(Player player) {
        int i = 0;

        if (this.KEY.equalsIgnoreCase("worlds")) {
            boolean isBlacklist = (boolean) ConfigManager.getValue("worlds.is-blacklist");
            ItemStack isBlacklistItemStack = Utils.createItemStack(isBlacklist ? Material.LIME_WOOL : Material.RED_WOOL, "&bworlds.is-blacklist", Collections.singletonList("&7" + isBlacklist));

            InventoryButton isBlackListButton = new InventoryButton().creator(p -> isBlacklistItemStack).clickConsumer(e -> {
                ConfigManager.updateValue("worlds.is-blacklist", !isBlacklist);
                Utils.getGuiManager().openGUI(new ConfigValueGUI(this.KEY), player);
            }).dragConsumer(e -> {
                ConfigManager.updateValue("worlds.is-blacklist", !isBlacklist);
                Utils.getGuiManager().openGUI(new ConfigValueGUI(this.KEY), player);
            });

            this.addButton(0, isBlackListButton);

            i++;

            for (String world : Stream.concat(ConfigManager.getWorldList().stream(), Bukkit.getWorlds().stream().map(WorldInfo::getName).toList().stream()).collect(Collectors.toSet())) {
                this.addButton(i, createWorldButton(player, world));
                i++;
            }
        }
        else {
            for (String key : this.KEYS) {
                var value = ConfigManager.getValue(this.KEY + "." + key);

                if (value instanceof MemorySection)
                    continue;

                List<String> lore = new ArrayList<>();
                lore.add(Utils.colorize("&7" + value));

                if (value instanceof Integer) {
                    lore.add("");
                    lore.add(ConfigManager.getMsgCmdConfigRightClick());
                    lore.add(ConfigManager.getMsgCmdConfigLeftClick());
                    lore.add(ConfigManager.getMsgCmdConfigShiftClick());
                }

                ItemStack is;
                if (key.equals("default-glow-color")) {
                    is = Utils.getColoredWool((String) value);
                    ItemMeta im = is.getItemMeta();
                    im.setDisplayName(Utils.colorize("&b" + key));
                    is.setItemMeta(im);
                }
                else if (this.KEY.startsWith("items")) {
                    is = Utils.colorizeItem((ItemStack) value);
                }
                else {
                    is = Utils.createItemStack(value instanceof Boolean ? (Boolean) value ? Material.LIME_WOOL : Material.RED_WOOL : Material.WHITE_WOOL, "&b" + key, lore);
                }

                InventoryButton button = new InventoryButton().creator(p -> is).clickConsumer(e -> {
                    if (this.KEY.startsWith("items")) {
                        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                            player.sendMessage(ConfigManager.getMsgCmdInvalidItem());
                            Utils.delayCloseInv(player);
                        }
                        else {
                            ConfigManager.updateValue(this.KEY + "." + key, player.getInventory().getItemInMainHand());
                            Utils.getGuiManager().openGUI(new ConfigValueGUI(this.KEY), player);
                        }
                    }
                    else if (this.KEY.startsWith("messages")) {
                        Utils.delayCloseInv(player);
                        player.sendMessage(ConfigManager.getMsgCmdConfigType());

                        String replacement = ConfigManager.CONFIGS_ARGS.getOrDefault(this.KEY + "." + key, Collections.emptyList()).toString();

                        player.sendMessage(ConfigManager.getMsgCmdConfigPlaceholders().replace("{placeholders}", replacement.equals("[]") ? "none" : replacement));
                        player.sendMessage(ConfigManager.getMsgCmdConfigCurrentValue().replace("{value}", value.toString()));
                        Constants.PLAYER_CONFIG_CHAT_MAP.put(player.getUniqueId().toString(), this.KEY + "." + key);
                    }
                    else if (key.equalsIgnoreCase("database-format")) {
                        int val = Constants.DATABASE_FORMATS.indexOf(ConfigManager.getDatabaseFormat());

                        if (val + 1 >= Constants.DATABASE_FORMATS.size()) {
                            val = 0;
                        }
                        else {
                            val++;
                        }

                        ConfigManager.updateValue(this.KEY + "." + key, Constants.DATABASE_FORMATS.get(val));
                        Utils.getGuiManager().openGUI(new ConfigValueGUI(this.KEY), player);
                    }
                    else if (key.equalsIgnoreCase("default-glow-color")) {
                        Utils.getGuiManager().openGUI(new ColorSelectGUI(this.KEY, ConfigManager.getGlowColor().name(), ChatColor.AQUA, false, chatcolor -> {
                            ConfigManager.updateValue(this.KEY + "." + key, chatcolor.name());
                            Utils.getGuiManager().openGUI(new ConfigValueGUI(this.KEY), player);
                        }), player);
                    }
                    else if (value instanceof Boolean) {
                        ConfigManager.updateValue(this.KEY + "." + key, !(Boolean) value);
                        Utils.getGuiManager().openGUI(new ConfigValueGUI(this.KEY), player);
                    }
                    else if (value instanceof Integer) {
                        if (e.isLeftClick()) {
                            if (e.isShiftClick()) {
                                ConfigManager.updateValue(this.KEY + "." + key, (Integer) value + 5);
                            }
                            else {
                                ConfigManager.updateValue(this.KEY + "." + key, (Integer) value + 1);
                            }
                        }
                        else if (e.isRightClick()) {
                            if (e.isShiftClick()) {
                                if ((Integer) value > 5) {
                                    ConfigManager.updateValue(this.KEY + "." + key, (Integer) value - 5);
                                }
                                else {
                                    ConfigManager.updateValue(this.KEY + "." + key, 0);
                                }
                            }
                            else {
                                if ((Integer) value > 0) {
                                    ConfigManager.updateValue(this.KEY + "." + key, (Integer) value - 1);
                                }
                            }
                        }

                        Utils.getGuiManager().openGUI(new ConfigValueGUI(this.KEY), player);
                    }
                }).dragConsumer(e -> {
                    if (this.KEY.startsWith("items")) {
                        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                            player.sendMessage(ConfigManager.getMsgCmdInvalidItem());
                            Utils.delayCloseInv(player);
                        }
                        else {
                            ConfigManager.updateValue(this.KEY + "." + key, player.getInventory().getItemInMainHand());
                            Utils.getGuiManager().openGUI(new ConfigValueGUI(this.KEY), player);
                        }
                    }
                    else if (this.KEY.startsWith("messages")) {
                        Utils.delayCloseInv(player);
                        player.sendMessage(ConfigManager.getMsgCmdConfigType());

                        String replacement = ConfigManager.CONFIGS_ARGS.getOrDefault(this.KEY + "." + key, Collections.emptyList()).toString();

                        player.sendMessage(ConfigManager.getMsgCmdConfigPlaceholders().replace("{placeholders}", replacement.equals("[]") ? "none" : replacement));
                        player.sendMessage(ConfigManager.getMsgCmdConfigCurrentValue().replace("{value}", value.toString()));
                        Constants.PLAYER_CONFIG_CHAT_MAP.put(player.getUniqueId().toString(), this.KEY + "." + key);
                    }
                    else if (key.equalsIgnoreCase("database-format")) {
                        int val = Constants.DATABASE_FORMATS.indexOf(ConfigManager.getDatabaseFormat());

                        if (val + 1 >= Constants.DATABASE_FORMATS.size()) {
                            val = 0;
                        }
                        else {
                            val++;
                        }

                        ConfigManager.updateValue(this.KEY + "." + key, Constants.DATABASE_FORMATS.get(val));
                        Utils.getGuiManager().openGUI(new ConfigValueGUI(this.KEY), player);
                    }
                    else if (key.equalsIgnoreCase("default-glow-color")) {
                        Utils.getGuiManager().openGUI(new ColorSelectGUI(this.KEY, ConfigManager.getGlowColor().name(), ChatColor.AQUA, false, chatcolor -> {
                            ConfigManager.updateValue(this.KEY + "." + key, chatcolor.name());
                            Utils.getGuiManager().openGUI(new ConfigValueGUI(this.KEY), player);
                        }), player);
                    }
                    else if (value instanceof Boolean) {
                        ConfigManager.updateValue(this.KEY + "." + key, !(Boolean) value);
                        Utils.getGuiManager().openGUI(new ConfigValueGUI(this.KEY), player);
                    }
                    else if (value instanceof Integer) {
                        ConfigManager.updateValue(this.KEY + "." + key, (Integer) value + 1);
                        Utils.getGuiManager().openGUI(new ConfigValueGUI(this.KEY), player);
                    }
                });

                this.addButton(i, button);
                i++;
            }
        }

        InventoryButton backButton = new InventoryButton().creator(p -> ConfigManager.getBackItem()).clickConsumer(e -> Utils.getGuiManager().openGUI(new ConfigGUI(), player)).dragConsumer(e -> Utils.getGuiManager().openGUI(new ConfigGUI(), player));
        this.addButton(SIZE - 1, backButton);

        super.decorate(player);
    }

    private InventoryButton createWorldButton(Player player, String world) {
        ItemStack is = Utils.createItemStack(ConfigManager.getWorldList().contains(world) ? Material.LIME_WOOL : Material.RED_WOOL, "&b" + world, List.of("&7" + ConfigManager.getWorldList().contains(world)));

        InventoryButton button = new InventoryButton().creator(p -> is).clickConsumer(e -> {
            List<String> newValue = new ArrayList<>(ConfigManager.getWorldList());

            if (ConfigManager.getWorldList().contains(world)) {
                newValue.remove(world);
            }
            else {
                newValue.add(world);
            }

            ConfigManager.updateValue("worlds.list", newValue);
            Utils.getGuiManager().openGUI(new ConfigValueGUI(this.KEY), player);
        }).dragConsumer(e -> {
            List<String> newValue = new ArrayList<>(ConfigManager.getWorldList());

            if (ConfigManager.getWorldList().contains(world)) {
                newValue.remove(world);
            }
            else {
                newValue.add(world);
            }

            ConfigManager.updateValue("worlds.list", newValue);
            Utils.getGuiManager().openGUI(new ConfigValueGUI(this.KEY), player);
        });

        return button;
    }

    @Override
    public void onClose(InventoryCloseEvent e) {

    }
}
