package net.maxxqc.mydrops.databases;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class IDatabase {
    protected final Map<UUID, DropPlayer> CACHE = new HashMap<>();

    public abstract String getGlowColor(Player player);
    public abstract void setGlowColor(Player player, ChatColor color);
    public abstract void setGlowColor(Player player, String color);
    public abstract void addTrustedPlayer(Player src, Player target);
    public abstract void removeTrustedPlayer(Player src, Player target);
    public abstract List<String> getTrustedPlayers(Player player);
    public abstract void load();
}
