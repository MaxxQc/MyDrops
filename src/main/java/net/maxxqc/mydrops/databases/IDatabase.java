package net.maxxqc.mydrops.databases;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class IDatabase {
    protected final Map<UUID, DropPlayer> CACHE = new HashMap<>();

    public String getGlowColor(Player src)
    {
        return this.getGlowColor(src.getUniqueId());
    }

    public abstract String getGlowColor(UUID src);

    public void setGlowColor(Player src, ChatColor color) {
        this.setGlowColor(src.getUniqueId(), color.name());
    }

    public void setGlowColor(UUID src, ChatColor color) {
        this.setGlowColor(src, color.name());
    }

    public  void setGlowColor(Player src, String color)
    {
        this.setGlowColor(src.getUniqueId(), color);
    }

    public abstract void setGlowColor(UUID src, String color);

    public void addTrustedPlayer(Player src, Player target)
    {
        this.addTrustedPlayer(src.getUniqueId(), target.getUniqueId().toString());
    }

    public void removeTrustedPlayer(Player src, Player target)
    {
        this.removeTrustedPlayer(src.getUniqueId(), target.getUniqueId().toString());
    }

    public void addTrustedPlayer(Player src, UUID target)
    {
        this.addTrustedPlayer(src.getUniqueId(), target.toString());
    }

    public void removeTrustedPlayer(Player src, UUID target)
    {
        this.removeTrustedPlayer(src.getUniqueId(), target.toString());
    }

    public void addTrustedPlayer(UUID src, Player target)
    {
        this.addTrustedPlayer(src, target.getUniqueId().toString());
    }

    public void removeTrustedPlayer(UUID src, Player target)
    {
        this.removeTrustedPlayer(src, target.getUniqueId().toString());
    }

    public void addTrustedPlayer(UUID src, UUID target)
    {
        this.addTrustedPlayer(src, target.toString());
    }

    public void removeTrustedPlayer(UUID src, UUID target)
    {
        this.removeTrustedPlayer(src, target.toString());
    }

    public abstract void addTrustedPlayer(UUID src, String target);

    public abstract void removeTrustedPlayer(UUID src, String target);

    public List<String> getTrustedPlayers(Player player)
    {
        return this.getTrustedPlayers(player.getUniqueId());
    }

    public abstract List<String> getTrustedPlayers(UUID player);

    public abstract void addTrustedParty(UUID src, String partyId);

    public void addTrustedParty(Player src, UUID partyId)
    {
        this.addTrustedParty(src.getUniqueId(), partyId.toString());
    }

    public void addTrustedParty(UUID src, UUID partyId)
    {
        this.addTrustedParty(src, partyId.toString());
    }

    public abstract void removeTrustedParty(UUID src, String partyId);

    public void removeTrustedParty(Player src, UUID partyId)
    {
        this.removeTrustedParty(src.getUniqueId(), partyId.toString());
    }

    public void removeTrustedParty(UUID src, UUID partyId)
    {
        this.removeTrustedParty(src, partyId.toString());
    }

    public List<String> getTrustedParties(Player src)
    {
        return this.getTrustedParties(src.getUniqueId());
    }

    public abstract List<String> getTrustedParties(UUID src);

    public abstract void load();

    public void close() {
        CACHE.clear();
    }
}
