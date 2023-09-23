package net.maxxqc.mydrops.mydrops;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Container;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
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

import java.util.UUID;

public final class MyDrops extends JavaPlugin implements Listener
{
    private final String MYDROPS_TAG = "mydrops-player-uuid";
    private final NamespacedKey NAMESPACED_KEY = new NamespacedKey(this, MYDROPS_TAG);

    @Override
    public void onEnable()
    {
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e)
    {
        e.getItemDrop().setOwner(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onBlockDrop(BlockDropItemEvent e)
    {
        e.getItems().forEach(i -> i.setOwner(e.getPlayer().getUniqueId()));
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
        //TODO set owner for the actual vehicle drop

        if (!(e.getVehicle() instanceof InventoryHolder))
            return;

        UUID uuid;

        if (e.getAttacker() instanceof Player)
            uuid = e.getAttacker().getUniqueId();
        else if (e.getAttacker() instanceof Projectile && ((Projectile) e.getAttacker()).getShooter() instanceof Player)
            uuid = ((Player) ((Projectile) e.getAttacker()).getShooter()).getUniqueId();
        else
            return;

        for (ItemStack is : ((InventoryHolder) e.getVehicle()).getInventory().getContents())
        {
            if (is == null) continue;
            setItemStackOwner(is, uuid);
            e.getVehicle().getWorld().dropItemNaturally(e.getVehicle().getLocation(), is);
        }
    }

    @EventHandler
    public void onHangingBreak(HangingBreakByEntityEvent e)
    {
        if (!(e.getRemover() instanceof Player))
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
        if (!(e.getEntity() instanceof ItemFrame))
            return;

        ItemFrame ifr = (ItemFrame) e.getEntity();
        UUID uuid;

        if (e.getDamager() instanceof Player)
            uuid = e.getDamager().getUniqueId();
        else if (e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player)
            uuid = ((Player) ((Projectile) e.getDamager()).getShooter()).getUniqueId();
        else
            return;

        //ifr.getPersistentDataContainer().set(NAMESPACED_KEY, PersistentDataType.STRING, uuid.toString());
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

        e.getEntity().setOwner(UUID.fromString(str));
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