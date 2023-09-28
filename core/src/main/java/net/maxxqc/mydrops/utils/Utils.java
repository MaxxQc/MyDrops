package net.maxxqc.mydrops.utils;

import fr.skytasul.glowingentities.GlowingEntities;
import net.maxxqc.mydrops.nms.NMSHandler;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.DrilldownPie;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

public class Utils
{
    public static JavaPlugin plugin;

    private static final String MYDROPS_TAG = "mydrops-owner";
    private static final String LEASH_TAG = "mydrops-leash";

    private static NamespacedKey namespaceKey;
    private static NMSHandler nmsHandler;
    private static GlowingEntities glowingEntities;

    public static void init(JavaPlugin plugin, int pluginId)
    {
        Utils.plugin = plugin;
        namespaceKey = new NamespacedKey(plugin, MYDROPS_TAG);
        ConfigManager.init(plugin);

        if (ConfigManager.hasBStats())
        {
            Metrics metrics = new Metrics(plugin, pluginId);
            metrics.addCustomChart(new SimplePie("option_glow", () -> String.valueOf(ConfigManager.hasOptionGlow())));

            if (ConfigManager.hasOptionGlow())
                metrics.addCustomChart(new SimplePie("option_glow-color", () -> ConfigManager.getGlowColor().toString()));

            metrics.addCustomChart(new SimplePie("protection_item-drop", () -> String.valueOf(ConfigManager.hasItemDropProtection())));
            metrics.addCustomChart(new SimplePie("protection_block-break", () -> String.valueOf(ConfigManager.hasBlockBreakProtection())));
            metrics.addCustomChart(new SimplePie("protection_item-frame-drop", () -> String.valueOf(ConfigManager.hasItemFrameDropProtection())));
            metrics.addCustomChart(new SimplePie("protection_vehicle-destroy", () -> String.valueOf(ConfigManager.hasVehicleDestroyProtection())));
            metrics.addCustomChart(new SimplePie("protection_hanging-break", () -> String.valueOf(ConfigManager.hasHangingBreakProtection())));
            metrics.addCustomChart(new SimplePie("protection_entity-kill", () -> String.valueOf(ConfigManager.hasEntityKillProtection())));
            metrics.addCustomChart(new SimplePie("protection_player-death", () -> String.valueOf(ConfigManager.hasPlayerDeathProtection())));

            metrics.addCustomChart(new DrilldownPie("minecraft_version", () ->
            {
                Map<String, Map<String, Integer>> map = new HashMap<>();
                String bukkitVersion = Bukkit.getServer().getVersion();
                Map<String, Integer> entry = new HashMap<>();
                entry.put(bukkitVersion, 1);
                map.put(bukkitVersion, entry);
                return map;
            }));

            plugin.getLogger().info("Successfully loaded bStats");
        }

        try
        {
            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);
            String[] verSplit = version.split("_");
            int centerVersion = Integer.parseInt(verSplit[1]);
            if (centerVersion >= 17 && ConfigManager.hasOptionGlow())
                if (!(centerVersion == 20 && version.split("_")[2].equals("R2")))
                    Utils.glowingEntities = new GlowingEntities(plugin);

            Utils.nmsHandler = (NMSHandler) Class.forName("net.maxxqc.mydrops.nms.NMSHandler_v" + version).newInstance();
        }
        catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public static void handleItemDrop(Item item, Player player)
    {
        if (player == null || player.hasPermission("mydrops.bypass.drop")) return;

        item.getPersistentDataContainer().set(namespaceKey, PersistentDataType.STRING, player.getUniqueId().toString());

        if (glowingEntities == null) return;

        try
        {
            glowingEntities.setGlowing(item, player, ConfigManager.getGlowColor());
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

    public static void shutdown()
    {
        if (glowingEntities == null) return;
        glowingEntities.disable();
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

    public static Block setBlockOwner(Block block, UUID uniqueId)
    {
        block.setMetadata(MYDROPS_TAG, new FixedMetadataValue(plugin, uniqueId));
        return block;
    }

    public static ItemStack getItemStackFromVehicle(Vehicle vehicle)
    {
        if (vehicle instanceof Boat) {
            return nmsHandler.getItemStackFromBoat(vehicle);
        } else if (vehicle instanceof Minecart) {
            return nmsHandler.getItemStackFromMinecart(vehicle);
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
}