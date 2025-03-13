package net.maxxqc.mydrops.databases.types;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import net.maxxqc.mydrops.databases.DropPlayer;
import net.maxxqc.mydrops.databases.IDatabase;
import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.ProtectionType;
import net.maxxqc.mydrops.utils.Utils;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.set;
import static com.mongodb.client.model.Updates.unset;

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
            createCollectionIfNotExists(db, ConfigManager.getDatabaseTablesPrefix() + "protections");
        } catch (Exception e) {
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
            } catch (Exception ignored) {}
            mongoClient = null;
            database = null;
        }
    }

    private DropPlayer loadDataFor(UUID player) {
        String glowColor = null;
        List<String> trustedPlayers = new ArrayList<>();
        List<String> trustedParties = new ArrayList<>();
        Map<ProtectionType, Boolean> protections = new HashMap<>();

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

        MongoCollection<Document> protectionsCollection = getDatabase().getCollection(ConfigManager.getDatabaseTablesPrefix() + "protections");
        for (Document doc : protectionsCollection.find(eq("uuid", uuid))) {
            Document protectionsDoc = doc.get("protections", Document.class);

            if (protectionsDoc == null) {
                continue;
            }

            for (Map.Entry<String, Object> entry : protectionsDoc.entrySet()) {
                ProtectionType protectionType = ProtectionType.fromValue(entry.getKey());
                if (protectionType != null) {
                    protections.put(protectionType, Boolean.parseBoolean(entry.getValue().toString()));
                }
            }
        }

        DropPlayer dropPlayer = new DropPlayer(player, glowColor, trustedPlayers, trustedParties, protections);
        CACHE.put(player, dropPlayer);

        return dropPlayer;
    }

    @Override
    public String getGlowColor(UUID player) {
        return getCachedOrLoad(player, DropPlayer::getGlowColor, ConfigManager.getGlowColor().name());
    }

    @Override
    public void setGlowColor(UUID player, String color) {
        if (color == null || color.equalsIgnoreCase(ConfigManager.getGlowColor().name())) {
            removePlayerData(player);
        } else {
            insertOrReplacePlayerData(player, color);
        }
    }

    private void removePlayerData(UUID player) {
        executeUpdate(ConfigManager.getDatabaseTablesPrefix() + "data", eq("uuid", player.toString()), null);
        updateCache(player, dp -> dp.setGlowColor(null));
    }

    private void insertOrReplacePlayerData(UUID player, String color) {
        Document dataDoc = new Document("uuid", player.toString()).append("glow-color", color);
        executeUpdate(ConfigManager.getDatabaseTablesPrefix() + "data", eq("uuid", player.toString()), dataDoc);
        updateCache(player, dp -> dp.setGlowColor(color));
    }

    @Override
    public void addTrustedPlayer(UUID src, String target) {
        Document trustDoc = new Document("uuid", src.toString()).append("trusted-uuid", target);
        executeUpdate(ConfigManager.getDatabaseTablesPrefix() + "trust", eq("uuid", src.toString()), trustDoc);
        updateCache(src, dp -> dp.addTrustedPlayer(target));
    }

    @Override
    public void removeTrustedPlayer(UUID src, String target) {
        executeUpdate(ConfigManager.getDatabaseTablesPrefix() + "trust", and(eq("uuid", src.toString()), eq("trusted-uuid", target)), null);
        updateCache(src, dp -> dp.removeTrustedPlayer(target));
    }

    @Override
    public void addTrustedParty(UUID src, String partyId) {
        Document trustDoc = new Document("uuid", src.toString()).append("trusted-party", partyId);
        executeUpdate(ConfigManager.getDatabaseTablesPrefix() + "trust", eq("uuid", src.toString()), trustDoc);
        updateCache(src, dp -> dp.addTrustedParty(partyId));
    }

    @Override
    public void removeTrustedParty(UUID src, String partyId) {
        executeUpdate(ConfigManager.getDatabaseTablesPrefix() + "trust", and(eq("uuid", src.toString()), eq("trusted-party", partyId)), null);
        updateCache(src, dp -> dp.removeTrustedParty(partyId));
    }

    @Override
    public List<String> getTrustedPlayers(UUID player) {
        return getCachedOrLoad(player, DropPlayer::getTrustedPlayers, Collections.emptyList());
    }

    @Override
    public List<String> getTrustedParties(UUID player) {
        return getCachedOrLoad(player, DropPlayer::getTrustedParties, Collections.emptyList());
    }

    @Override
    public void setProtection(UUID src, ProtectionType protectionType, boolean value) {
        String field = "protections." + protectionType.getStringValue();
        Bson filter = eq("uuid", src.toString());
        Bson update = value == ConfigManager.hasServerProtection(protectionType)
                ? unset(field)
                : set(field, value);
        executeUpdate(ConfigManager.getDatabaseTablesPrefix() + "protections", filter, update);
        updateCache(src, dp -> dp.setProtection(protectionType, value));
    }

    @Override
    public boolean getProtection(UUID src, ProtectionType protectionType) {
        return getCachedOrLoad(src, dp -> dp.getProtection(protectionType), ConfigManager.hasServerProtection(protectionType));
    }

    private void executeUpdate(String collectionName, Bson filter, Document update) {
        MongoCollection<Document> collection = getDatabase().getCollection(collectionName);
        if (update == null) {
            collection.deleteOne(filter);
        } else {
            collection.replaceOne(filter, update, new ReplaceOptions().upsert(true));
        }
    }

    private void executeUpdate(String collectionName, Bson filter, Bson update) {
        MongoCollection<Document> collection = getDatabase().getCollection(collectionName);
        collection.updateOne(filter, update, new UpdateOptions().upsert(true));
    }

    private <T> T getCachedOrLoad(UUID src, Function<DropPlayer, T> getter, T defaultValue) {
        if (CACHE.containsKey(src)) {
            return getter.apply(CACHE.get(src));
        }
        DropPlayer dropPlayer = loadDataFor(src);
        return dropPlayer == null ? defaultValue : getter.apply(dropPlayer);
    }

    private void updateCache(UUID src, Consumer<DropPlayer> updater) {
        if (CACHE.containsKey(src)) {
            updater.accept(CACHE.get(src));
        } else {
            loadDataFor(src);
        }
    }
}