package me.ichun.mods.limitedlives.loader.neoforge;

import me.ichun.mods.limitedlives.common.LimitedLives;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;

@Mod(LimitedLives.MOD_ID)
public class LoaderNeoforge extends LimitedLives
{
    public LoaderNeoforge(IEventBus modEventBus)
    {
        modProxy = this;

        //build the config
        ModConfigSpec.Builder configBuilder = new ModConfigSpec.Builder();
        config = new ConfigNeoforge(configBuilder);
        //register the config. This loads the config for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, configBuilder.build());

        LimitedLives.setEventHandlerServer(new EventHandlerServerNeoforge());
        NeoForge.EVENT_BUS.register(LimitedLives.eventHandlerServer);
    }
}
