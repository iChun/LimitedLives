package me.ichun.mods.limitedlives.loader.fabric;

import me.ichun.mods.limitedlives.common.core.EventHandlerServer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;

public class EventHandlerServerFabric extends EventHandlerServer
{
    public EventHandlerServerFabric()
    {
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
            super.onLivingDeath(entity);
            return true;
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            super.onPlayerRespawn(newPlayer, alive);
        });
    }
}
