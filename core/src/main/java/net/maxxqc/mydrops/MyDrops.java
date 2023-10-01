package net.maxxqc.mydrops;

import net.maxxqc.mydrops.commands.CommandDispatcher;
import net.maxxqc.mydrops.commands.CoreCommand;
import net.maxxqc.mydrops.commands.GlowColorCommand;
import net.maxxqc.mydrops.commands.ProtectionCommand;
import net.maxxqc.mydrops.protection.*;
import net.maxxqc.mydrops.utils.ConfigManager;
import net.maxxqc.mydrops.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class MyDrops extends JavaPlugin implements Listener
{
    @Override
    public void onEnable()
    {
        Utils.init(this, 19913);
        registerEventHandlers();
        registerCommands();
    }

    @Override
    public void onDisable()
    {
        Utils.shutdown();
    }

    private void registerEventHandlers()
    {
        Bukkit.getServer().getPluginManager().registerEvents(new ProtectionHandler(), this);

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
    }

    private void registerCommands()
    {
        CommandDispatcher handler = new CommandDispatcher();
        CoreCommand coreCmd = new CoreCommand();
        handler.register("mydrops", coreCmd);

        if (ConfigManager.hasPerPlayerGlow())
            handler.register("glowcolor", new GlowColorCommand());

        if (ConfigManager.hasPerPlayerProtection())
            handler.register("protection", new ProtectionCommand());

        getCommand("mydrops").setExecutor(handler);
        getCommand("mydrops").setTabCompleter(coreCmd);
    }
}