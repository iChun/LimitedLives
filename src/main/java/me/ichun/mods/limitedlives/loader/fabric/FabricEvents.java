package me.ichun.mods.limitedlives.loader.fabric;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;

public class FabricEvents
{
    private FabricEvents(){}//no init!
    public static final Event<PlayerTickEnd> PLAYER_TICK_END = EventFactory.createArrayBacked(PlayerTickEnd.class, callbacks -> player -> {
        for(PlayerTickEnd callback : callbacks)
        {
            callback.onPlayerTickEnd(player);
        }
    });

    @FunctionalInterface
    public interface PlayerTickEnd
    {
        void onPlayerTickEnd(Player player);
    }
}
