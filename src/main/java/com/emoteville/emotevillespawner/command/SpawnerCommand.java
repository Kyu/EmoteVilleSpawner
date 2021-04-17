package com.emoteville.emotevillespawner.command;

import com.emoteville.emotevillespawner.EmoteVilleSpawner;
import com.emoteville.emotevillespawner.util.TabCompleteHelper;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.TileEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.block.CraftBlock;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SpawnerCommand implements CommandExecutor {
    private final EmoteVilleSpawner plugin;

    public SpawnerCommand(EmoteVilleSpawner plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args[0].equalsIgnoreCase("menuchange")) {
            if (args.length >= 3) {
                // Get player info
                String playerName = args[2];
                Player p = this.plugin.getServer().getPlayer(playerName);
                if (p == null || !p.isOnline()) {
                    return false;
                }

                // Get spawner block, either from HashMap or from arguments
                Location spawnerLoc;
                Block spawner = null;

                if (args.length >= 6) {
                    String[] coords = Arrays.copyOfRange(args, 3, 6);
                    Integer[] numbers = new Integer[coords.length];
                    for (int i = 0; i < coords.length; i++) {
                        try {
                            numbers[i] = Integer.parseInt(coords[i]);
                        } catch (NumberFormatException nfe) {
                            return false;
                        }
                    }

                    spawner = p.getWorld().getBlockAt(numbers[0], numbers[1], numbers[2]);
                } else if (EmoteVilleSpawner.playerInteractedSpawners.containsKey(p.getUniqueId())) {
                    spawner = p.getWorld().getBlockAt(EmoteVilleSpawner.playerInteractedSpawners.get(p.getUniqueId()));
                }

                if (spawner == null || spawner.getType() != Material.SPAWNER) {
                    return false;
                }

                // Get spawner location, and for NBT purposes, get CraftWorld, CraftBlock, then TileEntity of block
                spawnerLoc = spawner.getLocation();

                org.bukkit.craftbukkit.v1_16_R3.CraftWorld craftWorld = (CraftWorld) p.getWorld();
                org.bukkit.craftbukkit.v1_16_R3.block.CraftBlock craftBlock = CraftBlock.at(craftWorld.getHandle(), new BlockPosition(spawnerLoc.getX(), spawnerLoc.getY(), spawnerLoc.getZ()));

                TileEntity tileEntity = craftWorld.getHandle().getTileEntity(craftBlock.getPosition());

                if (tileEntity != null) {
                    String spawnerType = args[1]; // Entity name TODO what if invalid, its now a pig
                    // Use SilkSpawner API to change spawner type
                    EmoteVilleSpawner.silkUtil.setSpawnerEntityID(spawner, spawnerType);

                    // get NBT info, as well as container, and bukkit custom data
                    net.minecraft.server.v1_16_R3.NBTTagCompound ntc = tileEntity.b();
                    net.minecraft.server.v1_16_R3.NBTTagCompound persitentContainer = tileEntity.persistentDataContainer.toTagCompound();
                    net.minecraft.server.v1_16_R3.NBTTagCompound bukkitValues = persitentContainer.getCompound("PublicBukkitValues");

                    /*
                    NBT Map For block
                    {
                        {-vanilla-nbt-here},
                        { "SpawnerChanges": {
                            "TotalChanges": -number-of-changes- (e.g 7),
                            "entity-one": [0, 3],
                            "entity-two": [1, 4, 6],
                            "entity-three": [2, 5]
                            }
                        }
                    }
                    Parsing:
                    changes -> number of changes
                    Order of changes -> get all strings and place them in array of [entity1, entity2, entity3, entity1, entity2, entity3, entity2] then output
                     */
                    // TODO this should be a function of itself

                    // Grab SpawnerChanges if it exists, or create a new one
                    net.minecraft.server.v1_16_R3.NBTTagCompound changeData;
                    if (!bukkitValues.hasKey("SpawnerChanges")) {
                        changeData = new net.minecraft.server.v1_16_R3.NBTTagCompound();
                        changeData.setInt("TotalChanges", 0);
                    } else {
                        changeData = bukkitValues.getCompound("SpawnerChanges");
                    }

                    // Grab changes if exists, or create new list of changes
                    ArrayList<Integer> changeArray = new ArrayList<>();
                    if (changeData.hasKey(spawnerType)) {
                        Arrays.stream(changeData.getIntArray(spawnerType)).forEach(changeArray::add);
                    }

                    // Update values from array
                    changeArray.add(changeData.getInt("TotalChanges"));
                    changeData.setInt("TotalChanges", changeData.getInt("TotalChanges") + 1);

                    changeData.setIntArray(spawnerType, changeArray.stream().mapToInt(i -> i).toArray());

                    // Update NBT mappings
                    bukkitValues.set("SpawnerChanges", changeData);
                    persitentContainer.set("PublicBukkitValues", bukkitValues);
                    tileEntity.persistentDataContainer.putAll(persitentContainer);

                    // For some reason a simple ntc.set() isn't persistent so I gotta do all this
                    tileEntity.save(ntc);

                    return true;
                }
            }
        }
        return false;
    }

    public static class TabComplete extends TabCompleteHelper implements TabCompleter {
        private final EmoteVilleSpawner plugin;

        public TabComplete(EmoteVilleSpawner plugin) {
            this.plugin = plugin;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            if (args.length == 0) {
                return null;
            }
            
            if (args.length == 1) {
                return Collections.singletonList("menuchange");
            }

            List<String> argv = new ArrayList<>();
            int indexInterest = 1;

            if (args[0].equalsIgnoreCase("menuchange")) {
                switch (args.length) {
                    case 2: // Find entity list (some entities listed are invalid) TODO filter out invalid (?)
                        indexInterest = 1;
                        argv = new ArrayList<>(EmoteVilleSpawner.silkUtil.getDisplayNameToMobID().values());
                        break;
                    case 3: // Find player list
                        indexInterest = 2;
                        Player[] playerList = this.plugin.getServer().getOnlinePlayers().toArray(new Player[]{});
                        for (Player p : playerList) {
                            argv.add(p.getName());
                        }
                        break;
                }
            }

            return getListOfStringsMatchingLastWord(argv, args[indexInterest]);
        }
    }
}
