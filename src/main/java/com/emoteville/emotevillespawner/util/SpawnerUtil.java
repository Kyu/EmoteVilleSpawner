package com.emoteville.emotevillespawner.util;

import net.minecraft.server.v1_16_R3.BlockPosition;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.block.CraftBlock;

public class SpawnerUtil {
    public static net.minecraft.server.v1_16_R3.TileEntity getTileEntityOfBlock(World world, Location location) {
        org.bukkit.craftbukkit.v1_16_R3.CraftWorld craftWorld = (CraftWorld) world;
        org.bukkit.craftbukkit.v1_16_R3.block.CraftBlock craftBlock = CraftBlock.at(craftWorld.getHandle(), new BlockPosition(location.getX(), location.getY(), location.getZ()));

        return craftWorld.getHandle().getTileEntity(craftBlock.getPosition());
    }
}
