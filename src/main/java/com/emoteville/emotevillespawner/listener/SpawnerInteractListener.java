package com.emoteville.emotevillespawner.listener;

import com.emoteville.emotevillespawner.EmoteVilleSpawner;
import com.emoteville.emotevillespawner.util.SpawnerUtil;
import com.emoteville.emotevillespawner.util.ToolHelper;
import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerBreakEvent;
import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerPlaceEvent;
import net.minecraft.server.v1_16_R3.TileEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;
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
            if (pickaxe.hasItemMeta() && pickaxe.getItemMeta().hasLore()) {
                int lvlEnchant = 0;

                List<String> lore = pickaxe.getItemMeta().getLore();
                for (String loreLine: lore) {
                    if (loreLine.contains("Spawner")) {
                        if (loreLine.split(" ")[1].equals("II")) {
                            lvlEnchant = 2;
                        } else if (loreLine.split(" ")[1].equals("I")) {
                            lvlEnchant = 1;
                        }
                    }
                }

                if (lvlEnchant >= 1) {
                    switch (lvlEnchant) {
                        case 1:
                            event.setDrop(new ItemStack(Material.SPAWNER, 1));
                            break;
                        case 2:
                        default:
                            ItemStack itemStack = event.getDrop();
                            if (itemStack == null || itemStack.getType() == Material.AIR) {
                                itemStack = EmoteVilleSpawner.silkUtil.newSpawnerItem(event.getSpawner().getSpawnedType().name(), "", 1,
                                        false);
                                itemStack.setType(Material.SPAWNER);
                            }

                            TileEntity te = SpawnerUtil.getTileEntityOfBlock(event.getSpawner().getWorld(), event.getSpawner().getLocation());
                            net.minecraft.server.v1_16_R3.NBTTagCompound blockData = te.persistentDataContainer.toTagCompound();

                            net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);

                            if (! blockData.isEmpty()) {
                                nmsItem.a("BlockChanges", blockData);
                            }

                            event.setDrop(CraftItemStack.asBukkitCopy(nmsItem));
                            break;
                    }
                    return;
                }
            }
        }

        event.setCancelled(true);
        event.getPlayer().sendMessage("Cannot break spawner without enchant!");
    }

    @EventHandler
    public void placeSpawnerEvent(SilkSpawnersSpawnerPlaceEvent event) {

        net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(event.getPlayer().getItemInHand());
        net.minecraft.server.v1_16_R3.NBTTagCompound c = nmsItem.getTag();

        if (c != null && c.hasKey("BlockChanges")) {
            if (c.get("BlockChanges") != null) {
                event.getSpawner().getBlock();
                Player p = event.getPlayer();

                Location spawnerLoc = event.getSpawner().getLocation();
                TileEntity tileEntity = SpawnerUtil.getTileEntityOfBlock(p.getWorld(), spawnerLoc);

                net.minecraft.server.v1_16_R3.NBTTagCompound ntc = tileEntity.b();
                ntc.set("PublicBukkitValues", c.get("BlockChanges"));

                tileEntity.load(null, ntc);

                tileEntity.save(ntc);
            }
        }
        // nmsItem.setTag(c);
    }
}
