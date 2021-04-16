package com.emoteville.emotevillespawner.listener;

import com.emoteville.emotevillespawner.EmoteVilleSpawner;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class SpawnerInteractListener implements Listener {
    private final EmoteVilleSpawner plugin;

    public SpawnerInteractListener(EmoteVilleSpawner plugin) {
        this.plugin = plugin;
    }

    // /spawner menuchange <type> <player> <coords>
    @EventHandler
    public void onPlayerShiftRightClick(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.SPAWNER
        && event.getPlayer().isSneaking() && event.getHand() == EquipmentSlot.HAND) {
            EmoteVilleSpawner.playerInteractedSpawners.put(
                    event.getPlayer().getUniqueId(), event.getClickedBlock().getLocation());
            event.getPlayer().performCommand(this.plugin.getSpawnerCmd());
        }
    }
}
