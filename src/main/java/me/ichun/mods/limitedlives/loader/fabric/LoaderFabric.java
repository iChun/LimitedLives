package me.ichun.mods.limitedlives.loader.fabric;

import me.ichun.mods.limitedlives.common.LimitedLives;
import me.lortseam.completeconfig.data.Config;
import net.fabricmc.api.ModInitializer;

public class LoaderFabric extends LimitedLives
    implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        modProxy = this;

        //register config
        ConfigFabric configFabric = new ConfigFabric();
        config = configFabric;
        Config modConfig = new Config(MOD_ID, new String[]{}, configFabric);
        modConfig.load();
        Runtime.getRuntime().addShutdownHook(new Thread(modConfig::save));

        LimitedLives.setEventHandlerServer(new EventHandlerServerFabric());
    }
}
