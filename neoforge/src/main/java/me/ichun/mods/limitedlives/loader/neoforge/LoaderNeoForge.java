package me.ichun.mods.limitedlives.loader.neoforge;

import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.limitedlives.common.LimitedLives;
import me.ichun.mods.limitedlives.common.core.Config;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(LimitedLives.MOD_ID)
public class LoaderNeoForge extends LimitedLives
{
    public LoaderNeoForge(IEventBus modEventBus)
    {
        modProxy = this;

        //register config
        config = iChunUtil.d().registerConfig(new Config(), modEventBus);

        if(FMLEnvironment.dist.isClient())
        {
            initClient();
        }

        LimitedLives.setEventHandlerServer(new EventHandlerServerNeoForge());
        NeoForge.EVENT_BUS.register(LimitedLives.eventHandlerServer);
    }


    @OnlyIn(Dist.CLIENT)
    private void initClient()
    {
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (modContainer, screen) -> new WorkspaceConfigs(screen));
    }
}
