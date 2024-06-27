package net.maxxqc.mydrops.utils;

import fr.skytasul.glowingentities.GlowingEntities;
import net.maxxqc.mydrops.inventory.GuiManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils
{
    public static JavaPlugin plugin;

    private static final String MYDROPS_TAG = "mydrops-owner";
    private static final String LEASH_TAG = "mydrops-leash";
    private static final Map<UUID, ItemStack[]> TRASH_CONTENT = new HashMap<>();

    private static GlowingEntities glowingEntities;
    private static NamespacedKey namespaceKey;
    private static GuiManager guiManager;

    public static void init(JavaPlugin plugin)
    {
        Utils.plugin = plugin;
        namespaceKey = new NamespacedKey(plugin, MYDROPS_TAG);
        ConfigManager.init(plugin);
        PlayerDataManager.init(plugin);

        if (guiManager == null)
            guiManager = new GuiManager();

        try {
            if (ConfigManager.hasOptionGlow())
                glowingEntities = new GlowingEntities(plugin);
        } catch (Exception e) {
            plugin.getLogger().warning("GlowingEntities not found. Glow indicator will be disabled.");
        }

        if (ConfigManager.hasBStats())
        {
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
    }

    public static void disableGlowingEntities() {
        if (glowingEntities != null) {
            glowingEntities.disable();
        }
    }

    public static void handleItemDrop(Item item, Player player)
    {
        if (player == null || player.hasPermission("mydrops.bypass.drop")) return;

        if (ConfigManager.isWorldListBlacklist())
        {
            //blacklist
            if (ConfigManager.getWorldList().contains(item.getWorld().getName()))
                return;
        }
        else
        {
            //whitelist
            if (!ConfigManager.getWorldList().contains(item.getWorld().getName()))
                return;
        }

        item.getPersistentDataContainer().set(namespaceKey, PersistentDataType.STRING, player.getUniqueId().toString());
        item.setInvulnerable(ConfigManager.hasOptionInvulnerable());

        if (ConfigManager.getPickupDelay() != 0) {
            item.setPickupDelay(ConfigManager.getPickupDelay() * 20);
        }

        if (!ConfigManager.hasOptionGlow()) return;

        ChatColor color = ConfigManager.hasPerPlayerGlow() ? PlayerDataManager.getGlowColor(player) : ConfigManager.getGlowColor();

        if (color == null) return;

        try {
            glowingEntities.setGlowing(item, player, color);
        }
        catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public static ItemStack setItemStackOwner(ItemStack is, UUID uniqueId, boolean clone)
    {
        if (clone) is = is.clone();
        ItemMeta im = is.getItemMeta();
        im.getPersistentDataContainer().set(namespaceKey, PersistentDataType.STRING, uniqueId.toString());
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack setItemStackOwner(ItemStack is, UUID uniqueId)
    {
        return setItemStackOwner(is, uniqueId, false);
    }

    public static void setBlockOwner(Block block, UUID uniqueId)
    {
        block.setMetadata(MYDROPS_TAG, new FixedMetadataValue(plugin, uniqueId));
    }

    public static ItemStack getItemStackFromVehicle(Vehicle vehicle)
    {
        if (vehicle instanceof Boat) {
            return getDropItemFromBoat((Boat) vehicle);
        } else if (vehicle instanceof Minecart) {
            return getDropItemFromMinecart((Minecart) vehicle);
        }

        return null;
    }

    public static UUID parseItem(Item item)
    {
        ItemStack is = item.getItemStack();
        ItemMeta im = is.getItemMeta();
        String str = im.getPersistentDataContainer().get(namespaceKey, PersistentDataType.STRING);
        im.getPersistentDataContainer().remove(namespaceKey);
        is.setItemMeta(im);
        return str == null ? null : UUID.fromString(str);
    }

    public static UUID parseEntity(Entity entity)
    {
        String value = entity.getPersistentDataContainer().get(namespaceKey, PersistentDataType.STRING);
        return value == null ? null : UUID.fromString(value);
    }

    public static void markEntityForLeash(Entity entity)
    {
        entity.setMetadata(LEASH_TAG, new FixedMetadataValue(plugin, "true"));
    }

    public static boolean parseLeashEntity(Entity entity)
    {
        if (!entity.hasMetadata(LEASH_TAG)) return false;
        boolean value = entity.getMetadata(LEASH_TAG).get(0).value().equals("true");
        entity.removeMetadata(LEASH_TAG, plugin);
        return value;
    }

    public static String colorize(String string)
    {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(string);

        while (matcher.find()) {
            String color = string.substring(matcher.start(), matcher.end());
            string = string.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
            matcher = pattern.matcher(string);
        }

        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String[] colorize(String... strings)
    {
        for (int i = 0; i < strings.length; i++)
            strings[i] = colorize(strings[i]);

        return strings;
    }

    public static ItemStack getDropItemFromBoat(Boat boat)
    {
        if (boat instanceof ChestBoat)
            return new ItemStack(Material.valueOf(boat.getBoatType() + "_CHEST_BOAT"));
        else
            return new ItemStack(Material.valueOf(boat.getBoatType() + "_BOAT"));
    }

    public static ItemStack getDropItemFromMinecart(Minecart minecart)
    {
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
        if (guiManager == null) guiManager = new GuiManager();
        return guiManager;
    }

    public static void colorizeItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(colorize(meta.getDisplayName()));
        meta.setLore(meta.getLore() == null ? null : List.of(colorize(meta.getLore().toArray(new String[meta.getLore().size()]))));
        item.setItemMeta(meta);
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
}