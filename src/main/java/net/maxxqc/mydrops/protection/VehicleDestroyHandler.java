package net.maxxqc.mydrops.protection;

import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.ProtectionType;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class VehicleDestroyHandler implements Listener
{
    @EventHandler
    private void onVehicleBreak(VehicleDestroyEvent e)
    {
        Player owner;

        if (e.getAttacker() instanceof Player)
            owner = (Player) e.getAttacker();
        else if (e.getAttacker() instanceof Projectile && ((Projectile) e.getAttacker()).getShooter() instanceof Player)
            owner = (Player) ((Projectile) e.getAttacker()).getShooter();
        else
            return;

        if (!ConfigManager.getDatabase().getProtection(owner, ProtectionType.VEHICLE_DESTROY))
            return;

        e.setCancelled(true);

        if (e.getVehicle() instanceof InventoryHolder)
        {
            for (ItemStack is : ((InventoryHolder) e.getVehicle()).getInventory())
            {
                if (is == null)
                    continue;
                e.getVehicle().getWorld().dropItemNaturally(e.getVehicle().getLocation(), Utils.setItemStackOwner(is, owner));
                ((InventoryHolder) e.getVehicle()).getInventory().remove(is);
            }
        }

        ItemStack itemStack = Utils.getItemStackFromVehicle(e.getVehicle());
        e.getVehicle().remove();

        // Creative players simply drop the content
        if (((Player) e.getAttacker()).getGameMode() == GameMode.CREATIVE) return;

        e.getVehicle().getWorld().dropItemNaturally(e.getVehicle().getLocation(), Utils.setItemStackOwner(itemStack, owner));
    }
}