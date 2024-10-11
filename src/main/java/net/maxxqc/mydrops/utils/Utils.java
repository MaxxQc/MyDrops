package net.maxxqc.mydrops.utils;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
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
import org.bukkit.plugin.Plugin;
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
    private static NamespacedKey namespacedKey;
    private static GuiManager guiManager;
    private static PartiesAPI partiesAPI;

    public static void init(JavaPlugin plugin) {
        Utils.plugin = plugin;
        namespacedKey = new NamespacedKey(plugin, MYDROPS_TAG);
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

        if (ConfigManager.hasHookParties()) {
            Plugin partiesPlugin = plugin.getServer().getPluginManager().getPlugin("Parties");
            if (partiesPlugin != null) {
                if (partiesPlugin.isEnabled()) {
                    partiesAPI = Parties.getApi();
                    plugin.getLogger().info("Parties is enabled, hooking into it");
                }
            }
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

                                    item.getPersistentDataContainer().remove(namespacedKey);
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

        String worldName = item.getWorld().getName();
        boolean isBlacklist = ConfigManager.isWorldListBlacklist();
        List<String> worldList = ConfigManager.getWorldList();

        if ((isBlacklist && worldList.contains(worldName)) || (!isBlacklist && !worldList.contains(worldName)))
            return;

        item.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, player.getUniqueId().toString());
        item.setInvulnerable(ConfigManager.hasOptionInvulnerable());

        if (ConfigManager.getHideDropsFromOthers() || ConfigManager.hasOptionGlow()) {
            String color = null;

            if (ConfigManager.hasOptionGlow()) {
                color = ConfigManager.hasPerPlayerGlow() ? ConfigManager.getDatabase().getGlowColor(player) : ConfigManager.getGlowColor().name();
            }

            ChatColor glowColor = null;
            if (color != null && !color.equalsIgnoreCase("none")) {
                try {
                    glowColor = ChatColor.valueOf(color);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }

            for (Player p : Bukkit.getOnlinePlayers()) {
                boolean canPickup = Utils.canPickup(p, item);

                if (ConfigManager.getHideDropsFromOthers() && !canPickup) {
                    p.hideEntity(plugin, item);
                }

                if (glowColor != null && canPickup) {
                    try {
                        glowingEntities.setGlowing(item, p, glowColor);
                    } catch (ReflectiveOperationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        int pickupDelay = ConfigManager.getPickupDelay();
        if (pickupDelay != 0) {
            item.setPickupDelay(pickupDelay * 20);
        }

        if (ConfigManager.hasProtectionExpiry() && !player.hasPermission("mydrops.bypass.expiry")) {
            PROTECTED_ITEMS.put(item.getUniqueId(), System.currentTimeMillis() + ConfigManager.getProtectionExpiry() * 1000L);
        }

    }

    public static ItemStack setItemStackOwner(ItemStack is, Player player, boolean clone) {
        if (clone)
            is = is.clone();

        ItemMeta im = is.getItemMeta();
        List<String> trustedPlayers = ConfigManager.getDatabase().getTrustedPlayers(player);
        trustedPlayers.add(player.getUniqueId().toString());
        if (im != null) {
            im.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, String.join(";", trustedPlayers));
        }
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
        String str = null;
        if (im != null) {
            str = im.getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING);
            im.getPersistentDataContainer().remove(namespacedKey);
        }
        is.setItemMeta(im);
        return str == null ? null : List.of(str.split(";"));
    }

    public static void markEntityForLeash(Entity entity) {
        entity.setMetadata(LEASH_TAG, new FixedMetadataValue(plugin, "true"));
    }

    public static boolean parseLeashEntity(Entity entity) {
        if (!entity.hasMetadata(LEASH_TAG))
            return false;
        boolean value = Objects.equals(entity.getMetadata(LEASH_TAG).get(0).value(), "true");
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
        if (meta != null) {
            meta.setDisplayName(colorize(meta.getDisplayName()));
            meta.setLore(meta.getLore() == null ? null : List.of(colorize(meta.getLore().toArray(new String[0]))));
        }
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
            if (meta != null) {
                meta.setDisplayName(colorize(name));
            }

        if (lore != null)
            if (meta != null) {
                meta.setLore(colorize(lore));
            }

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
        if (player.hasPermission("mydrops.bypass.pickup")) return true;

        String owner = item.getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING);

        if (owner == null) return true;

        UUID ownerUuid = UUID.fromString(owner);
        Set<String> canPickupList = new HashSet<>(ConfigManager.getDatabase().getTrustedPlayers(ownerUuid));

        Party ownerParty = partiesAPI.getPartyOfPlayer(ownerUuid);

        for (String partyUuid : ConfigManager.getDatabase().getTrustedParties(ownerUuid))
        {
            System.out.println("has a trusted party: " + partyUuid);

            Party party = partiesAPI.getParty(partyUuid);

            if (party == null && partyUuid.equals(owner))
            {
                party = ownerParty;

                if (party == null) continue;
            }

            if (party == null) continue;

            canPickupList.addAll(party.getMembers().stream().map(UUID::toString).toList());
        }

        canPickupList.add(owner);

        return canPickupList.contains(player.getUniqueId().toString());
    }

    public static void removeProtectedItem(UUID uniqueId) {
        PROTECTED_ITEMS.remove(uniqueId);
    }

    public static boolean isPartiesHooked() {
        return partiesAPI != null;
    }

    public static Party getPartyOfPlayer(Player source) {
        if (!isPartiesHooked()) return null;
        return partiesAPI.getParty(source.getUniqueId());
    }
}