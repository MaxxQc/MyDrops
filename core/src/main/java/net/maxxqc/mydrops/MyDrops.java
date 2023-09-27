package net.maxxqc.mydrops;

import fr.skytasul.glowingentities.GlowingEntities;
import net.maxxqc.mydrops.nms.NMSHandler;
import org.bukkit.*;
import org.bukkit.block.Container;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ListIterator;
import java.util.UUID;

public final class MyDrops extends JavaPlugin implements Listener
{
    private final String MYDROPS_TAG = "mydrops-player-uuid";
    private final NamespacedKey NAMESPACED_KEY = new NamespacedKey(this, MYDROPS_TAG);

    private NMSHandler nmsHandler;
    private GlowingEntities glowingEntities;

    @Override
    public void onEnable()
    {
        try
        {
            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);

            if (Integer.parseInt(version.split("_")[1]) >= 17)
                glowingEntities = new GlowingEntities(this);

            nmsHandler = (NMSHandler) Class.forName("net.maxxqc.mydrops.nms.NMSHandler_v" + version).newInstance();
            Bukkit.getServer().getPluginManager().registerEvents(this, this);
        }
        catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable()
    {
        if (glowingEntities == null) return;

        glowingEntities.disable();
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e)
    {
        handleItemDrop(e.getItemDrop(), e.getPlayer());
    }

    @EventHandler
    public void onBlockDrop(BlockDropItemEvent e)
    {
        e.getItems().forEach(i -> handleItemDrop(i, e.getPlayer()));
    }

    private void handleItemDrop(Item item, Player player)
    {
        if (player == null || player.hasPermission("mydrops.bypass.drop")) return;

        item.getPersistentDataContainer().set(NAMESPACED_KEY, PersistentDataType.STRING, player.getUniqueId().toString());

        if (glowingEntities == null) return;

        try
        {
            glowingEntities.setGlowing(item, player, ChatColor.AQUA);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getState() instanceof Container)
        {
            Container c = ((Container) e.getBlock().getState());

            for (ItemStack is : c.getInventory().getContents())
            {
                if (is == null)
                    continue;
                setItemStackOwner(is, e.getPlayer().getUniqueId());
            }
        }
        else
            e.getBlock().setMetadata(MYDROPS_TAG, new FixedMetadataValue(this, e.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void onVehicleBreak(VehicleDestroyEvent e)
    {
        UUID uuid;

        if (e.getAttacker() instanceof Player)
            uuid = e.getAttacker().getUniqueId();
        else if (e.getAttacker() instanceof Projectile && ((Projectile) e.getAttacker()).getShooter() instanceof Player)
            uuid = ((Player) ((Projectile) e.getAttacker()).getShooter()).getUniqueId();
        else
            return;

        e.setCancelled(true);

        if (e.getVehicle() instanceof InventoryHolder)
        {
            for (ItemStack is : ((InventoryHolder) e.getVehicle()).getInventory())
            {
                if (is == null)
                    continue;
                e.getVehicle().getWorld().dropItemNaturally(e.getVehicle().getLocation(), setItemStackOwner(is, uuid));
                ((InventoryHolder) e.getVehicle()).getInventory().remove(is);
            }
        }

        ItemStack itemStack = getItemStackFromVehicle(e.getVehicle());
        e.getVehicle().remove();

        // Creative players simply drop the content
        if (((Player) e.getAttacker()).getGameMode() == GameMode.CREATIVE) return;

        e.getVehicle().getWorld().dropItemNaturally(e.getVehicle().getLocation(), setItemStackOwner(itemStack, uuid));
    }

    private ItemStack getItemStackFromVehicle(Vehicle vehicle)
    {
        if (vehicle instanceof Boat) {
            return nmsHandler.getItemStackFromBoat(vehicle);
        } else if (vehicle instanceof Minecart) {
            return nmsHandler.getItemStackFromMinecart(vehicle);
        }
        return null;
    }

    @EventHandler
    public void onHangingBreak(HangingBreakByEntityEvent e)
    {
        if (!(e.getRemover() instanceof Player) || ((Player) e.getRemover()).getGameMode() == GameMode.CREATIVE)
            return;

        ItemStack is = null;

        if (e.getEntity() instanceof ItemFrame)
            is = new ItemStack(Material.ITEM_FRAME);
        else if (e.getEntity() instanceof GlowItemFrame)
            is = new ItemStack(Material.GLOW_ITEM_FRAME);
        else if (e.getEntity() instanceof LeashHitch)
            is = new ItemStack(Material.LEAD);
        else if (e.getEntity() instanceof Painting)
            is = new ItemStack(Material.PAINTING);

        if (is == null) return;

        e.setCancelled(true);
        e.getEntity().remove(); //TODO this thing spawns a lead
        setItemStackOwner(is, e.getRemover().getUniqueId());
        e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), is);
    }

    @EventHandler
    public void onEntityDmgByEntity(EntityDamageByEntityEvent e)
    {
        if (!(e.getEntity() instanceof ItemFrame) || e.getEntity() instanceof Vehicle)
            return;

        ItemFrame ifr = (ItemFrame) e.getEntity();
        UUID uuid;

        if (e.getDamager() instanceof Player)
            uuid = e.getDamager().getUniqueId();
        else if (e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player)
            uuid = ((Player) ((Projectile) e.getDamager()).getShooter()).getUniqueId();
        else
            return;

        ifr.setItem(setItemStackOwner(ifr.getItem(), uuid, true), false);
    }

    @EventHandler
    public void onKill(EntityDeathEvent e)
    {
        if (e.getEntity().getKiller() == null)
            return;

        UUID uuid;

        if (e.getEntity().getKiller() instanceof Player)
            uuid = e.getEntity().getKiller().getUniqueId();
        else if (e.getEntity().getKiller() instanceof Projectile && ((Projectile) e.getEntity().getKiller()).getShooter() instanceof Player)
            uuid = ((Player) ((Projectile) e.getEntity().getKiller()).getShooter()).getUniqueId();
        else
            return;

        e.getDrops().forEach(is -> setItemStackOwner(is, uuid));
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent e)
    {
        ItemStack is = e.getEntity().getItemStack();
        ItemMeta im = is.getItemMeta();
        String str = im.getPersistentDataContainer().get(NAMESPACED_KEY, PersistentDataType.STRING);
        im.getPersistentDataContainer().remove(NAMESPACED_KEY);
        is.setItemMeta(im);

        if (str == null)
            return;

        UUID ownerUUID = UUID.fromString(str);
        handleItemDrop(e.getEntity(), Bukkit.getPlayer(ownerUUID));
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e)
    {
        String uuidValue = e.getItem().getPersistentDataContainer().getOrDefault(NAMESPACED_KEY, PersistentDataType.STRING, "");
        if (!(e.getEntity() instanceof Player) || uuidValue.equals("") || e.getEntity().getUniqueId().toString().equals(uuidValue) || e.getEntity().hasPermission("mydrops.bypass.pickup")) return;
        e.setCancelled(true);
    }

    private ItemStack setItemStackOwner(ItemStack is, UUID uniqueId, boolean clone)
    {
        if (clone) is = is.clone();
        ItemMeta im = is.getItemMeta();
        im.getPersistentDataContainer().set(NAMESPACED_KEY, PersistentDataType.STRING, uniqueId.toString());
        is.setItemMeta(im);
        return is;
    }

    private ItemStack setItemStackOwner(ItemStack is, UUID uniqueId)
    {
        return setItemStackOwner(is, uniqueId, false);
    }
}