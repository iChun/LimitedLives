package me.ichun.mods.limitedlives.common.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class EntityHelper
{
    public static CompoundTag getPlayerPersistentData(Player player) //gets the persisted NBT.
    {
        CompoundTag persistentTag = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persistentTag);
        return persistentTag;
    }

    public static CompoundTag getPlayerPersistentData(Player player, String name) //gets a tag within the persisted NBT
    {
        CompoundTag persistentTag = getPlayerPersistentData(player).getCompound(name);
        getPlayerPersistentData(player).put(name, persistentTag);
        return persistentTag;
    }
}
