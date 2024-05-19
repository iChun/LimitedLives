package me.ichun.mods.limitedlives.loader.forge;

import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.limitedlives.common.LimitedLives;
import me.ichun.mods.limitedlives.common.core.Config;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(LimitedLives.MOD_ID)
public class LoaderForge extends LimitedLives
{
    public LoaderForge()
    {
        modProxy = this;

        //register config
        config = iChunUtil.d().registerConfig(new Config());

        LimitedLives.setEventHandlerServer(new EventHandlerServerForge());
        MinecraftForge.EVENT_BUS.register(LimitedLives.eventHandlerServer);
    }
}
