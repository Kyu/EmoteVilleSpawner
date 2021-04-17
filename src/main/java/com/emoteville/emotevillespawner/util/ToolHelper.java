package com.emoteville.emotevillespawner.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ToolHelper {
    public static boolean isPickaxe(ItemStack item) {
        Material type = item.getType();
        return type.equals(Material.NETHERITE_PICKAXE) || type.equals(Material.DIAMOND_PICKAXE)
                || type.equals(Material.IRON_PICKAXE) || type.equals(Material.GOLDEN_PICKAXE)
                || type.equals(Material.STONE_PICKAXE) || type.equals(Material.WOODEN_PICKAXE);
    }
}
