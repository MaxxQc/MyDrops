package net.maxxqc.mydrops.databases;

import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.ProtectionType;

import java.util.*;

public class DropPlayer {
    private final UUID uuid;
    private String glowColor;
    private final List<String> trustedPlayers;
    private final List<String> trustedParties;
    private final Map<ProtectionType, Boolean> protections;

    public DropPlayer(UUID uuid, String glowColor, List<String> trustedPlayers, List<String> trustedParties, Map<ProtectionType, Boolean> protections) {
        this.uuid = uuid;
        this.glowColor = glowColor;
        this.trustedPlayers = trustedPlayers;
        this.trustedParties = trustedParties;
        this.protections = protections;
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

    public boolean getProtection(ProtectionType protectionType) {
        return protections.getOrDefault(protectionType, ConfigManager.hasServerProtection(protectionType));
    }

    public void setProtection(ProtectionType protectionType, boolean value) {
        protections.put(protectionType, value);
    }

    public void removeProtection(ProtectionType protectionType) {
        protections.remove(protectionType);
    }
}
