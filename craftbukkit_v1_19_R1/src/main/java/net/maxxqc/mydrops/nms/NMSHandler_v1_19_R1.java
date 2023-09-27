package net.maxxqc.mydrops.nms;

import net.minecraft.world.entity.vehicle.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftBoat;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftMinecart;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NMSHandler_v1_19_R1 implements NMSHandler
{
    @Override
    public ItemStack getItemStackFromMinecart(Vehicle vehicle)
    {
        try
        {
            Method method = AbstractMinecart.class.getDeclaredMethod("h");
            method.setAccessible(true);
            return new ItemStack(Material.valueOf(
                    method.invoke((
                            (CraftMinecart) vehicle).getHandle())
                            .toString().toUpperCase()));
            //return CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack) method.invoke(((CraftMinecart) vehicle).getHandle()));
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
                    Boat.class.getMethod("h").invoke(((CraftBoat) vehicle).getHandle())
                            .toString().toUpperCase()));
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }
}