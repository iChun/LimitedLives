package me.ichun.mods.limitedlives.loader.neoforge;

import me.ichun.mods.limitedlives.common.core.EventHandlerServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Locale;

public class EventHandlerServerNeoforge extends EventHandlerServer
{
    public EventHandlerServerNeoforge()
    {
        super(new EntityPersistentDataHandlerNeoforge());
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
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.side.isServer() && event.phase == TickEvent.Phase.END && event.player.tickCount % 20 == 0)
        {
            onPlayerTickEnd(event.player);
        }
    }


    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event)
    {
        onRegisterCommands(event.getDispatcher());
    }

    @Override
    public void firePlayerTickEndEvent(Player player){}//Noop

    @Override
    public boolean isFabricEnv()
    {
        return false;
    }

    @Override
    public boolean isFakePlayer(ServerPlayer player)
    {
        return player.connection == null || player.getClass().getSimpleName().toLowerCase(Locale.ROOT).contains("fakeplayer");
    }
}
