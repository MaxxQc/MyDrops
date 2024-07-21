package net.maxxqc.mydrops.utils;

import fr.skytasul.glowingentities.GlowingEntities;
import net.maxxqc.mydrops.inventory.GuiManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static JavaPlugin plugin;

    private static final Map<UUID, Long> PROTECTED_ITEMS = new HashMap<>();
    private static final String MYDROPS_TAG = "mydrops-owner";
    private static final String LEASH_TAG = "mydrops-leash";
    private static final Map<UUID, ItemStack[]> TRASH_CONTENT = new HashMap<>();

    private static GlowingEntities glowingEntities;
    private static NamespacedKey namespaceKey;
    private static GuiManager guiManager;

    public static void init(JavaPlugin plugin) {
        Utils.plugin = plugin;
        namespaceKey = new NamespacedKey(plugin, MYDROPS_TAG);
        ConfigManager.init(plugin);

        if (guiManager == null)
            guiManager = new GuiManager();

        try {
            if (ConfigManager.hasOptionGlow())
                glowingEntities = new GlowingEntities(plugin);
        }
        catch (Exception e) {
            plugin.getLogger().warning("GlowingEntities not found. Glow indicator will be disabled.");
        }

        if (ConfigManager.hasBStats()) {
            Metrics metrics = new Metrics(plugin, Constants.BSTATS_PLUGIN_ID);
            metrics.addCustomChart(new SimplePie("option_glow", () -> String.valueOf(ConfigManager.hasOptionGlow())));

            if (ConfigManager.hasOptionGlow()) {
                metrics.addCustomChart(new SimplePie("option_glowcolor", () -> ConfigManager.getGlowColor().toString()));
            }

            metrics.addCustomChart(new SimplePie("protection_item-drop", () -> String.valueOf(ConfigManager.hasItemDropProtection())));
            metrics.addCustomChart(new SimplePie("protection_block-break", () -> String.valueOf(ConfigManager.hasBlockBreakProtection())));
            metrics.addCustomChart(new SimplePie("protection_item-frame-drop", () -> String.valueOf(ConfigManager.hasItemFrameDropProtection())));
            metrics.addCustomChart(new SimplePie("protection_vehicle-destroy", () -> String.valueOf(ConfigManager.hasVehicleDestroyProtection())));
            metrics.addCustomChart(new SimplePie("protection_hanging-break", () -> String.valueOf(ConfigManager.hasHangingBreakProtection())));
            metrics.addCustomChart(new SimplePie("protection_entity-kill", () -> String.valueOf(ConfigManager.hasEntityKillProtection())));
            metrics.addCustomChart(new SimplePie("protection_player-death", () -> String.valueOf(ConfigManager.hasPlayerDeathProtection())));

            plugin.getLogger().info("Successfully loaded bStats");
        }

        if (ConfigManager.hasProtectionExpiry()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Set<UUID> markedForRemoval = new HashSet<>();

                    PROTECTED_ITEMS.forEach((key, value) -> {
                        if (value < System.currentTimeMillis()) {
                            markedForRemoval.add(key);

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    Item item = (Item) Bukkit.getEntity(key);
                                    if (item == null)
                                        return;

                                    item.getPersistentDataContainer().remove(namespaceKey);
                                    item.setInvulnerable(false);

                                    if (!ConfigManager.getHideDropsFromOthers())
                                        return;

                                    for (Player p : Bukkit.getOnlinePlayers()) {
                                        p.showEntity(plugin, item);

                                        try {
                                            glowingEntities.unsetGlowing(item, p);
                                        }
                                        catch (ReflectiveOperationException ignored) {}
                                    }
                                }
                            }.runTask(plugin);
                        }
                    });

                    markedForRemoval.forEach(PROTECTED_ITEMS::remove);
                }
            }.runTaskTimerAsynchronously(plugin, 20L, 20L);
        }
    }

    public static void disableGlowingEntities() {
        if (glowingEntities != null) {
            glowingEntities.disable();
        }
    }

    public static void handleItemDrop(Item item, Player player) {
        if (player == null || player.hasPermission("mydrops.bypass.drop"))
            return;

        if (ConfigManager.isWorldListBlacklist()) {
            //blacklist
            if (ConfigManager.getWorldList().contains(item.getWorld().getName()))
                return;
        }
        else {
            //whitelist
            if (!ConfigManager.getWorldList().contains(item.getWorld().getName()))
                return;
        }

        List<String> trustedPlayers = new ArrayList<>(ConfigManager.getDatabase().getTrustedPlayers(player));
        trustedPlayers.add(player.getUniqueId().toString());
        item.getPersistentDataContainer().set(namespaceKey, PersistentDataType.STRING, String.join(";", trustedPlayers));

        item.setInvulnerable(ConfigManager.hasOptionInvulnerable());

        if (ConfigManager.getHideDropsFromOthers())
            for (Player p : Bukkit.getOnlinePlayers())
                if (p != null && !p.hasPermission("mydrops.bypass.pickup") && !trustedPlayers.contains(p.getUniqueId().toString()))
                    p.hideEntity(plugin, item);

        if (ConfigManager.getPickupDelay() != 0) {
            item.setPickupDelay(ConfigManager.getPickupDelay() * 20);
        }

        if (ConfigManager.hasProtectionExpiry() && !player.hasPermission("mydrops.bypass.expiry")) {
            PROTECTED_ITEMS.put(item.getUniqueId(), System.currentTimeMillis() + ConfigManager.getProtectionExpiry() * 1000L);
        }

        if (!ConfigManager.hasOptionGlow())
            return;

        String color = ConfigManager.hasPerPlayerGlow() ? ConfigManager.getDatabase().getGlowColor(player) : ConfigManager.getGlowColor().name();

        if (color == null || color.equalsIgnoreCase("none"))
            return;

        try {
            for (String uuid : trustedPlayers) {
                Player p = Bukkit.getPlayer(UUID.fromString(uuid));

                if (p != null) {
                    glowingEntities.setGlowing(item, p, ChatColor.valueOf(color));
                }
            }
        }
        catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public static ItemStack setItemStackOwner(ItemStack is, Player player, boolean clone) {
        if (clone)
            is = is.clone();

        ItemMeta im = is.getItemMeta();
        List<String> trustedPlayers = ConfigManager.getDatabase().getTrustedPlayers(player);
        trustedPlayers.add(player.getUniqueId().toString());
        im.getPersistentDataContainer().set(namespaceKey, PersistentDataType.STRING, String.join(";", trustedPlayers));
        is.setItemMeta(im);

        return is;
    }

    public static ItemStack setItemStackOwner(ItemStack is, Player player) {
        return setItemStackOwner(is, player, false);
    }

    public static void setBlockOwner(Block block, UUID uniqueId) {
        block.setMetadata(MYDROPS_TAG, new FixedMetadataValue(plugin, uniqueId));
    }

    public static ItemStack getItemStackFromVehicle(Vehicle vehicle) {
        if (vehicle instanceof Boat) {
            return getDropItemFromBoat((Boat) vehicle);
        }
        else if (vehicle instanceof Minecart) {
            return getDropItemFromMinecart((Minecart) vehicle);
        }

        return null;
    }

    public static List<String> parseItem(Item item) {
        ItemStack is = item.getItemStack();
        ItemMeta im = is.getItemMeta();
        String str = im.getPersistentDataContainer().get(namespaceKey, PersistentDataType.STRING);
        im.getPersistentDataContainer().remove(namespaceKey);
        is.setItemMeta(im);
        return str == null ? null : List.of(str.split(";"));
    }

    public static List<String> parseEntity(Entity entity) {
        String canPickupList = entity.getPersistentDataContainer().get(namespaceKey, PersistentDataType.STRING);
        return canPickupList == null ? null : List.of(canPickupList.split(";"));
    }

    public static void markEntityForLeash(Entity entity) {
        entity.setMetadata(LEASH_TAG, new FixedMetadataValue(plugin, "true"));
    }

    public static boolean parseLeashEntity(Entity entity) {
        if (!entity.hasMetadata(LEASH_TAG))
            return false;
        boolean value = entity.getMetadata(LEASH_TAG).get(0).value().equals("true");
        entity.removeMetadata(LEASH_TAG, plugin);
        return value;
    }

    public static String colorize(String string) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(string);

        while (matcher.find()) {
            String color = string.substring(matcher.start(), matcher.end());
            string = string.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
            matcher = pattern.matcher(string);
        }

        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String[] colorize(String... strings) {
        for (int i = 0; i < strings.length; i++)
            strings[i] = colorize(strings[i]);

        return strings;
    }

    public static List<String> colorize(List<String> strings) {
        List<String> list = new ArrayList<>(strings);
        list.replaceAll(Utils::colorize);
        return list;
    }

    public static ItemStack getDropItemFromBoat(Boat boat) {
        if (boat instanceof ChestBoat)
            return new ItemStack(Material.valueOf(boat.getBoatType() + "_CHEST_BOAT"));
        else
            return new ItemStack(Material.valueOf(boat.getBoatType() + "_BOAT"));
    }

    public static ItemStack getDropItemFromMinecart(Minecart minecart) {
        if (minecart instanceof ExplosiveMinecart)
            return new ItemStack(Material.TNT_MINECART);
        else if (minecart instanceof CommandMinecart)
            return new ItemStack(Material.COMMAND_BLOCK_MINECART);
        else if (minecart instanceof HopperMinecart)
            return new ItemStack(Material.HOPPER_MINECART);
        else if (minecart instanceof StorageMinecart)
            return new ItemStack(Material.CHEST_MINECART);
        else if (minecart instanceof PoweredMinecart)
            return new ItemStack(Material.FURNACE_MINECART);
        else
            return new ItemStack(Material.MINECART);
    }

    public static GuiManager getGuiManager() {
        if (guiManager == null)
            guiManager = new GuiManager();
        return guiManager;
    }

    public static ItemStack colorizeItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(colorize(meta.getDisplayName()));
        meta.setLore(meta.getLore() == null ? null : List.of(colorize(meta.getLore().toArray(new String[meta.getLore().size()]))));
        item.setItemMeta(meta);
        return item;
    }

    public static void saveTrashContent(Player player, ItemStack[] content) {
        TRASH_CONTENT.put(player.getUniqueId(), content);
    }

    public static void clearTrashContent(Player player) {
        TRASH_CONTENT.remove(player.getUniqueId());
    }

    public static boolean hasTrashContent(Player player) {
        return TRASH_CONTENT.containsKey(player.getUniqueId());
    }

    public static ItemStack[] getTrashContent(Player player) {
        ItemStack[] items = TRASH_CONTENT.get(player.getUniqueId());
        clearTrashContent(player);
        return items;
    }

    public static int nextDivisibleByNine(int number) {
        if (number % 9 == 0) {
            return number;
        }

        return ((number / 9) + 1) * 9;
    }

    public static ItemStack createItemStack(Material material, int amount, String name, List<String> lore) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();

        if (name != null)
            meta.setDisplayName(Utils.colorize(name));

        if (lore != null)
            meta.setLore(Utils.colorize(lore));

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItemStack(Material material, String name, List<String> lore) {
        return createItemStack(material, 1, name, lore);
    }

    public static ItemStack createItemStack(Material material, String name) {
        return createItemStack(material, 1, name, null);
    }

    public static ItemStack createItemStack(Material material) {
        return createItemStack(material, 1, null, null);
    }

    public static ItemStack getColoredWool(ChatColor color) {
        return createItemStack(Constants.ALL_COLORS.get(color.name()), color + color.name());
    }

    public static ItemStack getColoredWool(String color, String suffix) {
        return createItemStack(Constants.ALL_COLORS.get(color), ChatColor.valueOf(color) + color + suffix);
    }

    public static ItemStack getColoredWool(String color) {
        return getColoredWool(color, "");
    }

    public static void delayCloseInv(Player player) {
        Bukkit.getScheduler().runTaskLater(plugin, player::closeInventory, 1);
    }

    public static boolean canPickup(Player player, Item item) {
        List<String> canPickupList = Utils.parseEntity(item);
        return canPickupList == null || canPickupList.contains(player.getUniqueId().toString()) || player.hasPermission("mydrops.bypass.pickup");
    }

    public static void removeProtectedItem(UUID uniqueId) {
        PROTECTED_ITEMS.remove(uniqueId);
    }
}