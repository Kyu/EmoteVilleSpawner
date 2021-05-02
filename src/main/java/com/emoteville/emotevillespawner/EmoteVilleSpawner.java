package com.emoteville.emotevillespawner;

import com.emoteville.emotevillespawner.command.SpawnerCommand;
import com.emoteville.emotevillespawner.listener.SpawnerInteractListener;
import de.dustplanet.util.SilkUtil;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class EmoteVilleSpawner extends JavaPlugin {
    public static HashMap<UUID, Location> playerInteractedSpawners = new HashMap<>();
    public static SilkUtil silkUtil;
    private String spawnerCmd;  // command fired when spawner shift right clicked

    // Register events, command, config, dependencies
    @Override
    public void onEnable() {
        silkUtil = SilkUtil.hookIntoSilkSpanwers();
        this.getServer().getPluginManager().registerEvents(new SpawnerInteractListener(this), this);
        this.getCommand("spawner").setExecutor(new SpawnerCommand(this));
        this.getCommand("spawner").setTabCompleter(new SpawnerCommand.TabComplete(this));
        this.getConfig().options().copyDefaults(false);
        this.saveDefaultConfig();

        Configuration config = this.getConfig();
        spawnerCmd = config.getString("spawner_command");
    }

    @Override
    public void onDisable() {
    }

    public String getSpawnerCmd() {
        return this.spawnerCmd;
    }

}
