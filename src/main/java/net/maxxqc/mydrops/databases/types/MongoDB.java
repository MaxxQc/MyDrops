package net.maxxqc.mydrops.databases.types;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import net.maxxqc.mydrops.databases.DropPlayer;
import net.maxxqc.mydrops.databases.IDatabase;
import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.Utils;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import static com.mongodb.client.model.Filters.*;

public class MongoDB extends IDatabase {
    private MongoClient mongoClient;
    private MongoDatabase database;

    public MongoDatabase getDatabase() {
        if (database == null) {
            mongoClient = MongoClients.create(ConfigManager.getDatabaseUri());
            database = mongoClient.getDatabase(ConfigManager.getDatabaseName());
        }

        return database;
    }

    public void load() {
        try {
            MongoDatabase db = getDatabase();
            createCollectionIfNotExists(db, ConfigManager.getDatabaseTablesPrefix() + "data");
            createCollectionIfNotExists(db, ConfigManager.getDatabaseTablesPrefix() + "trust");
        }
        catch (Exception e) {
            Utils.plugin.getLogger().log(Level.SEVERE, "Error while loading database", e);
        }
    }

    private void createCollectionIfNotExists(MongoDatabase db, String collectionName) {
        db.getCollection(collectionName);
    }

    @Override
    public void close() {
        super.close();

        if (mongoClient != null) {
            try {
                mongoClient.close();
            }
            catch (Exception ignored) {}

            mongoClient = null;
            database = null;
        }
    }

    private DropPlayer loadDataFor(UUID player) {
        String glowColor = null;
        List<String> trustedPlayers = new ArrayList<>();
        List<String> trustedParties = new ArrayList<>();

        String uuid = player.toString();
        MongoCollection<Document> dataCollection = getDatabase().getCollection(ConfigManager.getDatabaseTablesPrefix() + "data");
        Document dataDoc = dataCollection.find(eq("uuid", uuid)).first();

        if (dataDoc != null) {
            glowColor = dataDoc.getString("glow-color");
        }

        MongoCollection<Document> trustCollection = getDatabase().getCollection(ConfigManager.getDatabaseTablesPrefix() + "trust");
        for (Document doc : trustCollection.find(eq("uuid", uuid))) {
            if (doc.containsKey("trusted-uuid")) {
                trustedPlayers.add(doc.getString("trusted-uuid"));
            }

            if (doc.containsKey("trusted-party")) {
                trustedParties.add(doc.getString("trusted-party"));
            }
        }

        DropPlayer dropPlayer = new DropPlayer(player, glowColor, trustedPlayers, trustedParties);
        CACHE.put(player, dropPlayer);

        return dropPlayer;
    }

    @Override
    public String getGlowColor(UUID player) {
        if (CACHE.containsKey(player)) {
            return CACHE.get(player).getGlowColor();
        }

        DropPlayer dropPlayer = loadDataFor(player);
        return dropPlayer.getGlowColor();
    }

    @Override
    public void setGlowColor(UUID player, String color) {
        if (color == null || color.equalsIgnoreCase(ConfigManager.getGlowColor().name())) {
            removePlayerData(player);
        }
        else {
            insertOrReplacePlayerData(player, color);
        }
    }

    private void removePlayerData(UUID player) {
        MongoCollection<Document> dataCollection = getDatabase().getCollection(ConfigManager.getDatabaseTablesPrefix() + "data");
        dataCollection.deleteOne(eq("uuid", player.toString()));

        if (CACHE.containsKey(player)) {
            CACHE.get(player).setGlowColor(null);
        }
    }

    private void insertOrReplacePlayerData(UUID player, String color) {
        String uuid = player.toString();
        MongoCollection<Document> dataCollection = getDatabase().getCollection(ConfigManager.getDatabaseTablesPrefix() + "data");

        Document dataDoc = new Document("uuid", uuid).append("glow-color", color);
        dataCollection.replaceOne(eq("uuid", uuid), dataDoc, new ReplaceOptions().upsert(true));

        if (CACHE.containsKey(player)) {
            CACHE.get(player).setGlowColor(color);
        }
        else {
            loadDataFor(player);
        }
    }

    @Override
    public void addTrustedPlayer(UUID src, String target) {
        String srcUuid = src.toString();
        MongoCollection<Document> trustCollection = getDatabase().getCollection(ConfigManager.getDatabaseTablesPrefix() + "trust");

        Document trustDoc = new Document("uuid", srcUuid).append("trusted-uuid", target);
        trustCollection.replaceOne(eq("uuid", srcUuid), trustDoc, new ReplaceOptions().upsert(true));

        if (CACHE.containsKey(src)) {
            CACHE.get(src).addTrustedPlayer(target);
        }
        else {
            loadDataFor(src);
        }
    }

    @Override
    public void removeTrustedPlayer(UUID src, String target) {
        String srcUuid = src.toString();
        MongoCollection<Document> trustCollection = getDatabase().getCollection(ConfigManager.getDatabaseTablesPrefix() + "trust");

        trustCollection.deleteOne(and(eq("uuid", srcUuid), eq("trusted-uuid", target)));

        if (CACHE.containsKey(src)) {
            CACHE.get(src).removeTrustedPlayer(target);
        }
    }

    @Override
    public void addTrustedParty(UUID src, String partyId) {
        String srcUuid = src.toString();
        MongoCollection<Document> trustCollection = getDatabase().getCollection(ConfigManager.getDatabaseTablesPrefix() + "trust");

        Document trustDoc = new Document("uuid", srcUuid).append("trusted-party", partyId);
        trustCollection.replaceOne(eq("uuid", srcUuid), trustDoc, new ReplaceOptions().upsert(true));

        if (CACHE.containsKey(src)) {
            CACHE.get(src).addTrustedParty(partyId);
        }
        else {
            loadDataFor(src);
        }
    }

    @Override
    public void removeTrustedParty(UUID src, String partyId) {
        MongoCollection<Document> trustCollection = getDatabase().getCollection(ConfigManager.getDatabaseTablesPrefix() + "trust");

        trustCollection.deleteOne(and(eq("uuid", src.toString()), eq("trusted-party", partyId)));

        if (CACHE.containsKey(src)) {
            CACHE.get(src).removeTrustedParty(partyId);
        }
    }

    @Override
    public List<String> getTrustedPlayers(UUID player) {
        if (CACHE.containsKey(player)) {
            return CACHE.get(player).getTrustedPlayers();
        }

        DropPlayer dropPlayer = loadDataFor(player);
        return dropPlayer.getTrustedPlayers();
    }

    @Override
    public List<String> getTrustedParties(UUID player) {
        if (CACHE.containsKey(player)) {
            return CACHE.get(player).getTrustedParties();
        }

        DropPlayer dropPlayer = loadDataFor(player);
        return dropPlayer.getTrustedParties();
    }
}
