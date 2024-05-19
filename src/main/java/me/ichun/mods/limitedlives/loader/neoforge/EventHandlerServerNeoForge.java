package me.ichun.mods.limitedlives.loader.neoforge;

import me.ichun.mods.ichunutil.loader.neoforge.EntityPersistentDataHandlerNeoForge;
import me.ichun.mods.limitedlives.common.core.EventHandlerServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class EventHandlerServerNeoForge extends EventHandlerServer
{
    public EventHandlerServerNeoForge()
    {
        super(new EntityPersistentDataHandlerNeoForge());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerDeath(LivingDeathEvent event)
    {
        onLivingDeath(event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        onPlayerRespawn((ServerPlayer)event.getEntity(), event.isEndConquered());
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event)
    {
        if(!event.getEntity().level().isClientSide() && event.getEntity().tickCount % 20 == 0)
        {
            onPlayerTickEnd(event.getEntity());
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event)
    {
        onRegisterCommands(event.getDispatcher());
    }
}
