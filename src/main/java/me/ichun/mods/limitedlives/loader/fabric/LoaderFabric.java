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
        configFabric.configInstance = new Config(MOD_ID, new String[]{}, configFabric);
        configFabric.configInstance.load();
        Runtime.getRuntime().addShutdownHook(new Thread(configFabric.configInstance::save));

        LimitedLives.setEventHandlerServer(new EventHandlerServerFabric());
    }
}
