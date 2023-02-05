package me.ichun.mods.limitedlives.loader.fabric;

import me.ichun.mods.limitedlives.common.core.EventHandlerServer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class EventHandlerServerFabric extends EventHandlerServer
{

    public EventHandlerServerFabric()
    {
        super(new EntityPersistentDataHandlerFabric());

        ServerPlayerEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
            super.onLivingDeath(entity);
            return true;
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            super.onPlayerRespawn(newPlayer, alive);
        });

        FabricEvents.PLAYER_TICK_END.register(player ->
        {
            if(!player.level.isClientSide() && player.tickCount % 20 == 0)
            {
                super.onPlayerTickEnd(player);
            }
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, isDedicated) -> onRegisterCommands(dispatcher));
    }

    @Override
    public void firePlayerTickEndEvent(Player player)
    {
        FabricEvents.PLAYER_TICK_END.invoker().onPlayerTickEnd(player);
    }

    @Override
    public boolean isFabricEnv()
    {
        return true;
    }

    @Override
    public boolean isFakePlayer(ServerPlayer player)
    {
        return player.connection == null;
    }
}
