package net.maxxqc.mydrops;

import net.maxxqc.mydrops.commands.*;
import net.maxxqc.mydrops.events.AutoUpdaterHandler;
import net.maxxqc.mydrops.protection.*;
import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.Constants;
import net.maxxqc.mydrops.utils.SpigetUpdater;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import static com.mojang.logging.LogUtils.getLogger;

public final class MyDrops extends JavaPlugin implements Listener
{
    @Override
    public void onEnable()
    {
        Utils.init(this);
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
                    Bukkit.getConsoleSender().sendMessage("A new update is available for MyDrops");
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

    @Override
    public void onDisable()
    {
        Utils.disableGlowingEntities();
    }

    private void registerCommands()
    {
        CommandDispatcher handler = new CommandDispatcher();
        CoreCommand coreCmd = new CoreCommand();
        handler.register("mydrops", coreCmd);

        if (ConfigManager.hasPerPlayerGlow())
            handler.register("glowcolor", new GlowColorCommand());

        ReloadCommand reloadCmd = new ReloadCommand();
        handler.register("reload", reloadCmd);
        handler.register("rl", reloadCmd);

        handler.register("config", new ConfigCommand());

        //if (ConfigManager.hasPerPlayerProtection()) TODO
        //    handler.register("protection", new ProtectionCommand());

        handler.register("trash", new TrashCommand());

        getCommand("mydrops").setExecutor(handler);
        getCommand("mydrops").setTabCompleter(coreCmd);
    }
}