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
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(LimitedLives.MOD_ID)
public class LoaderForge extends LimitedLives
{
    public LoaderForge()
    {
        modProxy = this;

        //register config
        config = iChunUtil.d().registerConfig(new Config());

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::initClient);

        LimitedLives.setEventHandlerServer(new EventHandlerServerForge());
        MinecraftForge.EVENT_BUS.register(LimitedLives.eventHandlerServer);
    }

    @OnlyIn(Dist.CLIENT)
    private void initClient()
    {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(WorkspaceConfigs::new));
    }
}
