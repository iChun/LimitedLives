package me.ichun.mods.limitedlives.loader.fabric;

import com.google.common.collect.MapMaker;
import me.ichun.mods.limitedlives.common.LimitedLives;
import me.ichun.mods.limitedlives.common.core.EntityPersistentDataHandler;
import me.ichun.mods.limitedlives.common.core.EventHandlerServer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Use on SERVER side only!
 */
public class EntityPersistentDataHandlerFabric
    implements EntityPersistentDataHandler
{
    public static final String DATA_KEY = LimitedLives.MOD_ID + "_persistent";

    public Map<Entity, CompoundTag> entToTagMap = new MapMaker().weakKeys().makeMap(); //We will let GC clean it up for us after the entity is marked as dead.

    public EntityPersistentDataHandlerFabric()
    {

        ServerLifecycleEvents.SERVER_STARTING.register(server -> clean());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> clean());

        //On entity world change. Not needed for players
        ServerEntityWorldChangeEvents.AFTER_ENTITY_CHANGE_WORLD.register((originalEntity, newEntity, origin, destination) ->
        {
            if(entToTagMap.containsKey(originalEntity))
            {
                entToTagMap.put(newEntity, entToTagMap.get(originalEntity));
                entToTagMap.remove(originalEntity);
            }
        });

        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) ->
        {
            CompoundTag oldPersistent = LimitedLives.eventHandlerServer.persistentDataHandler.getPersistentData(oldPlayer);
            if(oldPersistent.contains(EventHandlerServer.PLAYER_PERSISTED_NBT_TAG))
            {
                LimitedLives.eventHandlerServer.persistentDataHandler.getPersistentData(newPlayer).put(EventHandlerServer.PLAYER_PERSISTED_NBT_TAG, oldPersistent.get(EventHandlerServer.PLAYER_PERSISTED_NBT_TAG));
            }
        });
    }

    @Override
    public @NotNull CompoundTag getPersistentData(@NotNull Entity ent)
    {
        return entToTagMap.computeIfAbsent(ent, e -> new CompoundTag());
    }

    @Override
    public void savePersistentData(@NotNull Entity ent, @NotNull CompoundTag tag)
    {
        if(entToTagMap.containsKey(ent))
        {
            tag.put(DATA_KEY, entToTagMap.get(ent).copy()); //idk why Forge copies it but I'll copy it too I guess.
        }
    }

    @Override
    public void loadPersistentData(@NotNull Entity ent, @NotNull CompoundTag tag)
    {
        if(tag.contains(DATA_KEY, 10))
        {
            entToTagMap.put(ent, tag.getCompound(DATA_KEY));
        }
    }

    public void clean()
    {
        entToTagMap.clear();
    }
}
