package me.ichun.mods.limitedlives.loader.neoforge;

import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.limitedlives.common.LimitedLives;
import me.ichun.mods.limitedlives.common.core.Config;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(LimitedLives.MOD_ID)
public class LoaderNeoForge extends LimitedLives
{
    public LoaderNeoForge(IEventBus modEventBus)
    {
        modProxy = this;

        //register config
        config = iChunUtil.d().registerConfig(new Config(), modEventBus);

        LimitedLives.setEventHandlerServer(new EventHandlerServerNeoForge());
        NeoForge.EVENT_BUS.register(LimitedLives.eventHandlerServer);
    }
}
