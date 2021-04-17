package com.emoteville.emotevillespawner.command;

import com.emoteville.emotevillespawner.EmoteVilleSpawner;
import com.emoteville.emotevillespawner.util.TabCompleteHelper;
import com.emoteville.emotevillespawner.util.ToolHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GiveSpawnerEnchantCommand implements CommandExecutor {
    EmoteVilleSpawner plugin;

    public GiveSpawnerEnchantCommand(EmoteVilleSpawner plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        Player toEnchant;
        if (args.length >= 1) {
            String playerName = args[0];
            Player p = this.plugin.getServer().getPlayer(playerName);
            if (p != null && p.isOnline()) {
                toEnchant = p;
            } else {
                return false;
            }
        } else {
            return false;
        }
        int level;

        try {
            level = Integer.parseInt(args[1]);
        } catch (NumberFormatException | IndexOutOfBoundsException e ) {
            level = 1;
        }

        ItemStack mainHandItem = toEnchant.getInventory().getItemInMainHand();
        if (ToolHelper.isPickaxe(mainHandItem)) {
            mainHandItem.addEnchantment(this.plugin.SPAWNER_ENCHANT, level);
        } else {
            return false;
        }
        return true;
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

            // TODO DRY
            List<String> argv = new ArrayList<>();
            int indexInterest = 0;

            switch (args.length) {
                case 1: // Find player list
                    Player[] playerList = this.plugin.getServer().getOnlinePlayers().toArray(new Player[]{});
                    for (Player p : playerList) {
                        argv.add(p.getName());
                    }
                    break;
                case 2: // Find entity list (some entities listed are invalid) TODO filter out invalid (?)
                    indexInterest = 1;
                    argv = Arrays.asList("1", "2");
                    break;
            }

            return getListOfStringsMatchingLastWord(argv, args[indexInterest]);
        }
    }
}
