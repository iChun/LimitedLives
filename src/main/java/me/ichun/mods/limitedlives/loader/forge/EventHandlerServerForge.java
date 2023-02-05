package me.ichun.mods.limitedlives.loader.forge;

import me.ichun.mods.limitedlives.common.core.EventHandlerServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandlerServerForge extends EventHandlerServer
{
    public EventHandlerServerForge()
    {
        super(new EntityPersistentDataHandlerForge());
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
        return player instanceof FakePlayer || player.connection == null; // || player.getName().getUnformattedComponentText().toLowerCase().startsWith("fakeplayer") || player.getName().getUnformattedComponentText().toLowerCase().startsWith("[minecraft]");
    }
}
