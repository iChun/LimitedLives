package me.ichun.mods.limitedlives.loader.neoforge;

import me.ichun.mods.limitedlives.common.core.EntityPersistentDataHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class EntityPersistentDataHandlerNeoforge
    implements EntityPersistentDataHandler
{
    @Override
    public @NotNull CompoundTag getPersistentData(@NotNull Entity ent)
    {
        return ent.getPersistentData();
    }

    @Override
    public void savePersistentData(@NotNull Entity ent, @NotNull CompoundTag tag){} //NOP - Forge already does persistent data.

    @Override
    public void loadPersistentData(@NotNull Entity ent, @NotNull CompoundTag tag){} //NOP - Forge already does persistent data.
}
