package com.emoteville.emotevillespawner;

import com.emoteville.emotevillespawner.command.GiveSpawnerEnchantCommand;
import com.emoteville.emotevillespawner.command.SpawnerCommand;
import com.emoteville.emotevillespawner.enchant.SpawnerEnchant;
import com.emoteville.emotevillespawner.listener.SpawnerInteractListener;
import de.dustplanet.util.SilkUtil;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.UUID;

public class EmoteVilleSpawner extends JavaPlugin {
    public SpawnerEnchant SPAWNER_ENCHANT = new SpawnerEnchant(this);
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
        this.getCommand("spawnerenchant").setExecutor(new GiveSpawnerEnchantCommand(this));
        this.getCommand("spawnerenchant").setTabCompleter(new GiveSpawnerEnchantCommand.TabComplete(this));

        this.getConfig().options().copyDefaults(false);
        this.saveDefaultConfig();

        Configuration config = this.getConfig();
        spawnerCmd = config.getString("spawner_command");

        this.registerEnchant();
    }

    @Override
    public void onDisable() {
    }

    public String getSpawnerCmd() {
        return this.spawnerCmd;
    }

    public void registerEnchant() {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        SpawnerEnchant spawnerEnchant = new SpawnerEnchant(this);
        Enchantment.registerEnchantment(spawnerEnchant);

    }

}
