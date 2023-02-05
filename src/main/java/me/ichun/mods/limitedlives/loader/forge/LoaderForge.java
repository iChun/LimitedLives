package me.ichun.mods.limitedlives.loader.forge;

import me.ichun.mods.limitedlives.common.LimitedLives;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(LimitedLives.MOD_ID)
public class LoaderForge extends LimitedLives
{
    public LoaderForge()
    {
        modProxy = this;

        //build the config
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
        config = new ConfigForge(configBuilder);
        //register the config. This loads the config for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, configBuilder.build());

        LimitedLives.setEventHandlerServer(new EventHandlerServerForge());
        MinecraftForge.EVENT_BUS.register(LimitedLives.eventHandlerServer);
    }
}
