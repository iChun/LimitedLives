package me.ichun.mods.limitedlives.common.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class EntityHelper
{
    public static NBTTagCompound getPlayerPersistentData(EntityPlayer player) //gets the persisted NBT.
    {
        NBTTagCompound persistentTag = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        player.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persistentTag);
        return persistentTag;
    }

    public static NBTTagCompound getPlayerPersistentData(EntityPlayer player, String name) //gets a tag within the persisted NBT
    {
        NBTTagCompound persistentTag = getPlayerPersistentData(player).getCompoundTag(name);
        getPlayerPersistentData(player).setTag(name, persistentTag);
        return persistentTag;
    }
}
