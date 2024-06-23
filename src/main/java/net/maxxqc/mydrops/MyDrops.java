package net.maxxqc.mydrops;

import net.maxxqc.mydrops.commands.*;
import net.maxxqc.mydrops.events.AutoUpdaterHandler;
import net.maxxqc.mydrops.events.JoinLeaveHandler;
import net.maxxqc.mydrops.protection.*;
import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.Constants;
import net.maxxqc.mydrops.utils.SpigetUpdater;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class MyDrops extends JavaPlugin implements Listener
{
    @Override
    public void onEnable()
    {
        Utils.init(this);
        registerEventHandlers();
        registerCommands();

        if (!ConfigManager.hasAutoUpdateChecker()) return;

        Constants.setCurrentVersion(getDescription().getVersion());
        SpigetUpdater updater = new SpigetUpdater(getDescription().getVersion());
        BukkitRunnable updaterRunnable = new BukkitRunnable() {
            @Override
            public void run()
            {
                if (updater.checkForUpdate())
                {
                    Constants.markUpdateAvailable(updater.getUpdatedVersion());

                    Bukkit.getConsoleSender().sendMessage(Utils.colorize("------------------------------------------------------------------------"));
                    Bukkit.getConsoleSender().sendMessage("A new update is avaiable for MyDrops");
                    Bukkit.getConsoleSender().sendMessage("Current version: " + Constants.CURRENT_VERSION);
                    Bukkit.getConsoleSender().sendMessage("Latest version: " + Constants.UPDATER_NEW_VERSION);
                    Bukkit.getConsoleSender().sendMessage("Download on https://www.spigotmc.org/resources/mydrops." + Constants.SPIGOT_RESOURCE_ID + "/");
                    Bukkit.getConsoleSender().sendMessage(Utils.colorize("------------------------------------------------------------------------"));

                    cancel();
                }
            }
        };
        updaterRunnable.runTaskTimerAsynchronously(this, 0L, 600L);
    }

    private void registerEventHandlers()
    {
        Bukkit.getServer().getPluginManager().registerEvents(new ProtectionHandler(), this);

        if (ConfigManager.hasOptionGlow())
            Bukkit.getServer().getPluginManager().registerEvents(new JoinLeaveHandler(), this);

        if (ConfigManager.hasItemDropProtection())
            Bukkit.getServer().getPluginManager().registerEvents(new ItemDropHandler(), this);

        if (ConfigManager.hasBlockBreakProtection())
            Bukkit.getServer().getPluginManager().registerEvents(new BlockBreakHandler(), this);

        if (ConfigManager.hasVehicleDestroyProtection())
            Bukkit.getServer().getPluginManager().registerEvents(new VehicleDestroyHandler(), this);

        if (ConfigManager.hasHangingBreakProtection())
            Bukkit.getServer().getPluginManager().registerEvents(new HangingBreakHandler(), this);

        if (ConfigManager.hasItemFrameDropProtection())
            Bukkit.getServer().getPluginManager().registerEvents(new ItemFrameDropHandler(), this);

        if (ConfigManager.hasEntityKillProtection())
            Bukkit.getServer().getPluginManager().registerEvents(new EntityKillHandler(), this);

        if (ConfigManager.hasPlayerDeathProtection())
            Bukkit.getServer().getPluginManager().registerEvents(new PlayerDeathHandler(), this);

        if (ConfigManager.hasMythicMobsProtection() && getServer().getPluginManager().isPluginEnabled("MythicMobs")) {
            getLogger().info("MythicMobs is enabled, hooking into it for event handling");
            Bukkit.getServer().getPluginManager().registerEvents(new MythicMobsHandler(), this);
        }

        if (ConfigManager.hasAutoUpdateChecker())
            Bukkit.getServer().getPluginManager().registerEvents(new AutoUpdaterHandler(), this);
    }

    private void registerCommands()
    {
        CommandDispatcher handler = new CommandDispatcher();
        CoreCommand coreCmd = new CoreCommand();
        handler.register("mydrops", coreCmd);

        if (ConfigManager.hasPerPlayerGlow())
            handler.register("glowcolor", new GlowColorCommand());

        //if (ConfigManager.hasPerPlayerProtection()) TODO
        //    handler.register("protection", new ProtectionCommand());

        handler.register("trash", new TrashCommand());

        getCommand("mydrops").setExecutor(handler);
        getCommand("mydrops").setTabCompleter(coreCmd);
    }
}