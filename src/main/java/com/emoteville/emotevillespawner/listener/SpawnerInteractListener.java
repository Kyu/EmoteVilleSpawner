package com.emoteville.emotevillespawner.listener;

import com.emoteville.emotevillespawner.EmoteVilleSpawner;
import com.emoteville.emotevillespawner.util.ToolHelper;
import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerBreakEvent;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

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

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock().getType().equals(Material.SPAWNER)
        && event.getPlayer().isSneaking() && Objects.equals(event.getHand(), EquipmentSlot.HAND)) { // Main Hand
            EmoteVilleSpawner.playerInteractedSpawners.put(
                    event.getPlayer().getUniqueId(), event.getClickedBlock().getLocation());
            event.getPlayer().performCommand(this.plugin.getSpawnerCmd());
        }
    }

    @EventHandler
    public void breakSpawnerEvent(SilkSpawnersSpawnerBreakEvent event) {
        if (event.getPlayer() == null) {
            return;
        }

        if (ToolHelper.isPickaxe(event.getPlayer().getInventory().getItemInMainHand())) {
            ItemStack pickaxe = event.getPlayer().getInventory().getItemInMainHand();
            if (pickaxe.hasItemMeta()) {
                if (pickaxe.getItemMeta().getEnchantLevel(this.plugin.SPAWNER_ENCHANT) >= 1) {
                    return;
                }
            }
        }

        event.getSpawner().getWorld().playEffect(
                event.getSpawner().getLocation(), Effect.MOBSPAWNER_FLAMES, 2
        );
        event.getSpawner().getBlock().breakNaturally(null); // TODO playSound maybe?
        event.setCancelled(true);
    }
}
