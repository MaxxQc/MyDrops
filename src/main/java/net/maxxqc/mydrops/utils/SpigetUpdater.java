package net.maxxqc.mydrops.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.FileSystems;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

// Based on https://github.com/DarkerMinecraft/PluginUpdater/blob/main/src/main/java/com/darkerminecraft/SpigotPlugin.java
public class SpigetUpdater
{
    private final String version;
    private String updatedVersion;

    private String pluginName;
    private boolean isExternal, isPremium;

    public SpigetUpdater(String version)
    {
        this.version = version;
        gatheredInformation();
    }

    public boolean checkForUpdate()
    {
        try {
            JsonElement updatedVersion = parseJSON("https://api.spiget.org/v2/resources/" + Constants.SPIGOT_RESOURCE_ID + "/versions/latest");
            this.updatedVersion = updatedVersion.getAsJsonObject().get("name").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return !version.equals(updatedVersion);
    }

    private void gatheredInformation() {
        try {
            JsonElement info = parseJSON("https://api.spiget.org/v2/resources/" + Constants.SPIGOT_RESOURCE_ID);
            JsonObject jsonObj = info.getAsJsonObject();
            this.isExternal = jsonObj.get("external").getAsBoolean();
            this.pluginName = jsonObj.get("name").getAsString();
            this.isPremium = jsonObj.get("premium").getAsBoolean();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JsonElement parseJSON(String link) throws Exception
    {
        URL url = new URL(link);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.addRequestProperty("User-Agent", "PLUGINS");
        InputStream inputStream = connection.getInputStream();
        InputStreamReader reader = new InputStreamReader(inputStream);
        return JsonParser.parseReader(reader);
    }

    public String getUpdatedVersion()
    {
        return updatedVersion;
    }
}

enum DownloadMessage
{
    SUCCESS, FAILED, EXTERNALDOWNLOAD, ALREADYUPDATED, PREMIUMRESOURCE
}