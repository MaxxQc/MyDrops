package net.maxxqc.mydrops.events;

import net.maxxqc.mydrops.utils.Constants;
import net.maxxqc.mydrops.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class AutoUpdaterHandler implements Listener
{
    @EventHandler
    private void onJoin(PlayerJoinEvent e)
    {
        if (!Constants.updateAvailable() || !e.getPlayer().hasPermission("mydrops.update")) return;

        BaseComponent component = new TextComponent(new ComponentBuilder( "A new update is available for ").color(ChatColor.YELLOW)
                .append("MyDrops\n").color(ChatColor.GOLD).bold(true)
                .append("Current version: ").color(ChatColor.YELLOW).bold(false)
                .append(Constants.CURRENT_VERSION + "\n").color(ChatColor.GOLD)
                .append("Latest version: ").color(ChatColor.YELLOW)
                .append(Constants.UPDATER_NEW_VERSION + "\n").color(ChatColor.GOLD)
                .append("Download on ").color(ChatColor.YELLOW)
                .append("https://spigotmc.org/").color(ChatColor.GOLD)
                .create());
        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/mydrops." + Constants.SPIGOT_RESOURCE_ID + "/"));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click on this message to open up the plugin page")));

        e.getPlayer().sendMessage(Utils.colorize("&e&m                                                                        "));
        e.getPlayer().spigot().sendMessage(component);
        e.getPlayer().sendMessage(Utils.colorize("&e&m                                                                        "));
    }
}