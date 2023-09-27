package net.maxxqc.mydrops.nms;

import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.ItemStack;

public interface NMSHandler
{
    ItemStack getItemStackFromMinecart(Vehicle vehicle);
    ItemStack getItemStackFromBoat(Vehicle vehicle);
}