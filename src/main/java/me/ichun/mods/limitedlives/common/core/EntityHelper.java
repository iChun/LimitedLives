package me.ichun.mods.limitedlives.common.core;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public class EntityHelper
{
    public static CompoundNBT getPlayerPersistentData(PlayerEntity player) //gets the persisted NBT.
    {
        CompoundNBT persistentTag = player.getPersistentData().getCompound(PlayerEntity.PERSISTED_NBT_TAG);
        player.getPersistentData().put(PlayerEntity.PERSISTED_NBT_TAG, persistentTag);
        return persistentTag;
    }

    public static CompoundNBT getPlayerPersistentData(PlayerEntity player, String name) //gets a tag within the persisted NBT
    {
        CompoundNBT persistentTag = getPlayerPersistentData(player).getCompound(name);
        getPlayerPersistentData(player).put(name, persistentTag);
        return persistentTag;
    }
}
