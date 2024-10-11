package net.maxxqc.mydrops.databases;

import net.maxxqc.mydrops.utils.ConfigManager;

import java.util.List;
import java.util.UUID;

public class DropPlayer {
    private final UUID uuid;
    private String glowColor;
    private final List<String> trustedPlayers;
    private final List<String> trustedParties;

    public DropPlayer(UUID uuid, String glowColor, List<String> trustedPlayers, List<String> trustedParties) {
        this.uuid = uuid;
        this.glowColor = glowColor;
        this.trustedPlayers = trustedPlayers;
        this.trustedParties = trustedParties;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getGlowColor() {
        return glowColor == null ? ConfigManager.getGlowColor().name() : glowColor;
    }

    public void setGlowColor(String glowColor) {
        this.glowColor = glowColor;
    }

    public List<String> getTrustedPlayers() {
        return trustedPlayers;
    }

    public void addTrustedPlayer(String player) {
        if (trustedPlayers.contains(player) || player.equals(uuid.toString()))
            return;

        trustedPlayers.add(player);
    }

    public void removeTrustedPlayer(String player) {
        trustedPlayers.remove(player);
    }


    public void addTrustedParty(String partyId) {
        if (trustedParties.contains(partyId))
            return;

        trustedParties.add(partyId);
    }

    public void removeTrustedParty(String partyId) {
        trustedParties.remove(partyId);
    }

    public List<String> getTrustedParties() {
        return trustedParties;
    }
}
