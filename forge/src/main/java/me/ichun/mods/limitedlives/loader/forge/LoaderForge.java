package me.ichun.mods.limitedlives.loader.forge;

import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.limitedlives.common.LimitedLives;
import me.ichun.mods.limitedlives.common.core.Config;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(LimitedLives.MOD_ID)
public class LoaderForge extends LimitedLives
{
    public LoaderForge(FMLJavaModLoadingContext context)
    {
        modProxy = this;

        //register config
        config = iChunUtil.d().registerConfig(new Config(), context);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> initClient(context));

        LimitedLives.setEventHandlerServer(new EventHandlerServerForge());
        MinecraftForge.EVENT_BUS.register(LimitedLives.eventHandlerServer);
    }

    @OnlyIn(Dist.CLIENT)
    private void initClient(FMLJavaModLoadingContext context)
    {
        context.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> new WorkspaceConfigs(screen, MOD_ID)));
    }
}
