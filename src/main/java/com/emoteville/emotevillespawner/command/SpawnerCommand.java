package com.emoteville.emotevillespawner.command;

import com.emoteville.emotevillespawner.EmoteVilleSpawner;
import com.emoteville.emotevillespawner.util.TabCompleteHelper;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;

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
                Block spawner = null;

                if (args.length >= 6) {
                    String[] coords = Arrays.copyOfRange(args, 4, 7);
                    Integer[] numbers = new Integer[coords.length];
                    for (int i = 0; i < coords.length; i++) {
                        try {
                            numbers[i] = Integer.parseInt(coords[i]);
                        } catch (NumberFormatException nfe) {
                            return false;
                        }
                    }  // TODO coord detection doesn't work

                    spawner = p.getWorld().getBlockAt(numbers[0], numbers[1], numbers[2]);
                } else if (EmoteVilleSpawner.playerInteractedSpawners.containsKey(p.getUniqueId())) {
                    spawner = p.getWorld().getBlockAt(EmoteVilleSpawner.playerInteractedSpawners.get(p.getUniqueId()));
                }

                // SilkSpawner API to change block type
                if (spawner != null && spawner.getType() == Material.SPAWNER) {
                    String spawnerType = args[1]; // String must be of namespace:entity_name e.g minecraft:pig
                    EmoteVilleSpawner.silkUtil.setSpawnerEntityID(spawner, spawnerType);
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
                    case 2: // Find entity list (some entities listed are invalid) TODO fix (?)
                        indexInterest = 1;
                        EntityType[] entityTypes = EntityType.values();
                        for (EntityType e : entityTypes) {
                            argv.add(e.name().toLowerCase(Locale.ROOT));
                        }
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
