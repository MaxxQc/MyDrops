package net.maxxqc.mydrops.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ConfirmInventory implements InventoryHolder
{
    private final Inventory INV;

    public ConfirmInventory()
    {
        INV = Bukkit.createInventory(this, 9);

        ItemStack yesItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta yesMeta = yesItem.getItemMeta();
        yesMeta.setDisplayName(ConfigManager.getTxtConfirmYes());
        yesItem.setItemMeta(yesMeta);

        ItemStack noItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta noMeta = noItem.getItemMeta();
        noMeta.setDisplayName(ConfigManager.getTxtConfirmNo());
        noItem.setItemMeta(noMeta);

        INV.setItem(2, yesItem);
        INV.setItem(6, noItem);
    }

    @Override
    public Inventory getInventory()
    {
        return INV;
    }
}