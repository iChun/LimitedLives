package me.ichun.mods.limitedlives.loader.fabric;

import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.limitedlives.common.LimitedLives;
import me.ichun.mods.limitedlives.common.core.Config;
import net.fabricmc.api.ModInitializer;

public class LoaderFabric extends LimitedLives
    implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        modProxy = this;

        //register config
        config = iChunUtil.d().registerConfig(new Config());

        LimitedLives.setEventHandlerServer(new EventHandlerServerFabric());
    }
}
