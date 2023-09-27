package net.maxxqc.mydrops.nms;

import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftBoat;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftMinecart;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class NMSHandler_v1_20_R1 implements NMSHandler
{
    @Override
    public ItemStack getItemStackFromMinecart(Vehicle vehicle)
    {
        try
        {
            return CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack)
                    Minecart.class.getMethod("j").invoke(((CraftMinecart) vehicle).getHandle()));
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public ItemStack getItemStackFromBoat(Vehicle vehicle)
    {
        try
        {
            return new ItemStack(Material.valueOf(
                    Boat.class.getMethod("j").invoke(((CraftBoat) vehicle).getHandle())
                            .toString().toUpperCase()));
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }
}