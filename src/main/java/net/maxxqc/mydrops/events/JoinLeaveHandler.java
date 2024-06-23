package net.maxxqc.mydrops.events;

import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinLeaveHandler implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    private void onJoin(PlayerJoinEvent e) {
        Utils.createGlowingTeams(e.getPlayer());
    }
}
