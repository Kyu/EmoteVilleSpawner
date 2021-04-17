package com.emoteville.emotevillespawner.enchant;

import com.emoteville.emotevillespawner.EmoteVilleSpawner;
import com.emoteville.emotevillespawner.util.ToolHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class SpawnerEnchant extends Enchantment {
    public SpawnerEnchant(EmoteVilleSpawner plugin) {
        super(new NamespacedKey(plugin, "spawner"));
    }

    @Override
    public String getName() {
        return "Spawner";
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.TOOL;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(Enchantment enchantment) {
        return enchantment.equals(Enchantment.LOOT_BONUS_BLOCKS);
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return ToolHelper.isPickaxe(itemStack);
    }
}
