package net.maxxqc.mydrops.databases.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.internal.diagnostics.logging.Loggers;
import net.maxxqc.mydrops.databases.DropPlayer;
import net.maxxqc.mydrops.databases.IDatabase;
import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.Utils;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

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

    private DropPlayer loadDataFor(Player player) {
        String glowColor = null;
        List<String> trustedPlayers = new ArrayList<>();

        String uuid = player.getUniqueId().toString();
        MongoCollection<Document> dataCollection = getDatabase().getCollection(ConfigManager.getDatabaseTablesPrefix() + "data");
        Document dataDoc = dataCollection.find(eq("uuid", uuid)).first();

        if (dataDoc != null) {
            glowColor = dataDoc.getString("glow-color");
        }

        MongoCollection<Document> trustCollection = getDatabase().getCollection(ConfigManager.getDatabaseTablesPrefix() + "trust");
        for (Document doc : trustCollection.find(eq("uuid", uuid))) {
            trustedPlayers.add(doc.getString("trusted-uuid"));
        }

        DropPlayer dropPlayer = new DropPlayer(player.getUniqueId(), glowColor, trustedPlayers);
        CACHE.put(player.getUniqueId(), dropPlayer);

        return dropPlayer;
    }

    public String getGlowColor(Player player) {
        if (CACHE.containsKey(player.getUniqueId())) {
            return CACHE.get(player.getUniqueId()).getGlowColor();
        }

        DropPlayer dropPlayer = loadDataFor(player);
        return dropPlayer.getGlowColor();
    }

    public void setGlowColor(Player player, ChatColor color) {
        setGlowColor(player, color == null ? null : color.name());
    }

    public void setGlowColor(Player player, String color) {
        if (color == null || color.equalsIgnoreCase(ConfigManager.getGlowColor().name())) {
            removePlayerData(player);
        }
        else {
            insertOrReplacePlayerData(player, color);
        }
    }

    private void removePlayerData(Player player) {
        String uuid = player.getUniqueId().toString();
        MongoCollection<Document> dataCollection = getDatabase().getCollection(ConfigManager.getDatabaseTablesPrefix() + "data");
        dataCollection.deleteOne(eq("uuid", uuid));

        if (CACHE.containsKey(player.getUniqueId())) {
            CACHE.get(player.getUniqueId()).setGlowColor(null);
        }
    }

    private void insertOrReplacePlayerData(Player player, String color) {
        String uuid = player.getUniqueId().toString();
        MongoCollection<Document> dataCollection = getDatabase().getCollection(ConfigManager.getDatabaseTablesPrefix() + "data");

        Document dataDoc = new Document("uuid", uuid).append("glow-color", color);
        dataCollection.replaceOne(eq("uuid", uuid), dataDoc, new ReplaceOptions().upsert(true));

        if (CACHE.containsKey(player.getUniqueId())) {
            CACHE.get(player.getUniqueId()).setGlowColor(color);
        }
        else {
            loadDataFor(player);
        }
    }

    public void addTrustedPlayer(Player src, Player target) {
        String srcUuid = src.getUniqueId().toString();
        String targetUuid = target.getUniqueId().toString();
        MongoCollection<Document> trustCollection = getDatabase().getCollection(ConfigManager.getDatabaseTablesPrefix() + "trust");

        Document trustDoc = new Document("uuid", srcUuid).append("trusted-uuid", targetUuid);
        trustCollection.replaceOne(eq("uuid", srcUuid), trustDoc, new ReplaceOptions().upsert(true));

        if (CACHE.containsKey(src.getUniqueId())) {
            CACHE.get(src.getUniqueId()).addTrustedPlayer(targetUuid);
        }
        else {
            loadDataFor(src);
        }
    }

    public void removeTrustedPlayer(Player src, Player target) {
        String srcUuid = src.getUniqueId().toString();
        String targetUuid = target.getUniqueId().toString();
        MongoCollection<Document> trustCollection = getDatabase().getCollection(ConfigManager.getDatabaseTablesPrefix() + "trust");

        trustCollection.deleteOne(and(eq("uuid", srcUuid), eq("trusted-uuid", targetUuid)));

        if (CACHE.containsKey(src.getUniqueId())) {
            CACHE.get(src.getUniqueId()).removeTrustedPlayer(targetUuid);
        }
    }

    public List<String> getTrustedPlayers(Player player) {
        if (CACHE.containsKey(player.getUniqueId())) {
            return CACHE.get(player.getUniqueId()).getTrustedPlayers();
        }

        DropPlayer dropPlayer = loadDataFor(player);
        return dropPlayer.getTrustedPlayers();
    }
}
