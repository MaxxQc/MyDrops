package net.maxxqc.mydrops.protection;

import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class HangingBreakHandler implements Listener
{
    @EventHandler
    private void onHangingBreak(HangingBreakByEntityEvent e)
    {
        if (!(e.getRemover() instanceof Player))
            return;

        Player player = (Player) e.getRemover();

        if (e.getEntity() instanceof LeashHitch)
        {
            e.setCancelled(true);
            handleLeash(e.getEntity(), player);
            return;
        }

        if (((Player) e.getRemover()).getGameMode() == GameMode.CREATIVE) return;

        ItemStack is = null;

        if (e.getEntity() instanceof GlowItemFrame)
            is = new ItemStack(Material.GLOW_ITEM_FRAME);
        else if (e.getEntity() instanceof ItemFrame)
            is = new ItemStack(Material.ITEM_FRAME);
        else if (e.getEntity() instanceof Painting)
            is = new ItemStack(Material.PAINTING);

        if (is == null) return;

        e.setCancelled(true);
        e.getEntity().remove();
        Utils.setItemStackOwner(is, player);
        e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), is);
    }

    @EventHandler
    public void onLeashDrop(EntityDropItemEvent e)
    {
        if (e.getItemDrop().getItemStack().getType() != Material.LEAD || !(Utils.parseLeashEntity(e.getEntity()))) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent e)
    {
        if (!(e.getRightClicked() instanceof LeashHitch)) return;
        e.setCancelled(true);
        handleLeash(e.getRightClicked(), e.getPlayer());
    }

    private void handleLeash(Entity entity, Player player)
    {
        //https://www.spigotmc.org/threads/prevent-break-fence-if-animal-is-attached-to-it-code-works-but-is-it-correct.102571/
        List<LivingEntity> attachedEntities = entity.getNearbyEntities(10, 10, 10).stream() // Creates the stream
                .filter(ent -> ent instanceof LivingEntity) // Filters all entities that aren't living entities away.
                .map(ent -> (LivingEntity) ent) // Converts the stream into a stream of living entities
                .filter(LivingEntity::isLeashed) // Filters all of the unleashed entities away.
                .filter(ent -> ent.getLeashHolder() instanceof LeashHitch) // Filters all non-LeashHitches away
                .filter(ent -> ent.getLeashHolder().getUniqueId() == entity.getUniqueId())
                .toList();
        attachedEntities.forEach(Utils::markEntityForLeash);
        ItemStack is = new ItemStack(Material.LEAD, attachedEntities.size());
        entity.remove();
        Utils.setItemStackOwner(is, player);
        entity.getWorld().dropItemNaturally(entity.getLocation(), is);
    }
}