package me.ichun.mods.limitedlives.loader.neoforge;

import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.limitedlives.common.LimitedLives;
import me.ichun.mods.limitedlives.common.core.Config;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

import java.util.function.Supplier;

@Mod(LimitedLives.MOD_ID)
public class LoaderNeoForge extends LimitedLives
{
    public LoaderNeoForge(IEventBus modEventBus, ModContainer container)
    {
        modProxy = this;

        //register config
        config = iChunUtil.d().registerConfig(new Config(), modEventBus, container);

        if(FMLEnvironment.dist.isClient())
        {
            initClient(container);
        }

        LimitedLives.setEventHandlerServer(new EventHandlerServerNeoForge());
        NeoForge.EVENT_BUS.register(LimitedLives.eventHandlerServer);
    }


    @OnlyIn(Dist.CLIENT)
    private void initClient(ModContainer container)
    {
        container.registerExtensionPoint(IConfigScreenFactory.class, (Supplier<IConfigScreenFactory>)() -> (modContainer, screen) -> new WorkspaceConfigs(screen, MOD_ID));
    }
}
