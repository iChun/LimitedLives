package me.ichun.mods.limitedlives.common;

import me.ichun.mods.limitedlives.common.core.EventHandler;
import me.ichun.mods.limitedlives.common.core.Reference;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MODID, name = Reference.NAME,
        version = Reference.VERSION,
        guiFactory = "me.ichun.mods.limitedlives.common.core.GuiFactory",
        acceptableRemoteVersions = "*",
        dependencies = "required-after:forge@[13.19.0.2141,)",
        acceptedMinecraftVersions = "[1.12,1.13)"
)
public class LimitedLives
{
    @Mod.Instance(Reference.MODID)
    public static LimitedLives instance;

    public static Configuration config;

    public static int maxLives = 20;
    public static int banType = 1;
    public static int banTime = 5 * 60;
    public static int healthAdjust = 1;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        config = new Configuration(event.getSuggestedConfigurationFile());
        syncConfig();

        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    public static void syncConfig()
    {
        String cat = "general";
        config.setCategoryComment("general", "General Settings");
        maxLives = config.getInt("maxLives", cat, 20, 1, Integer.MAX_VALUE, I18n.translateToLocal("limitedlives.config.prop.maxLives.comment"));
        banType = config.getInt("banType", cat, 1, 1, 2, I18n.translateToLocal("limitedlives.config.prop.banType.comment"));
        banTime = config.getInt("banTime", cat, 5 * 60, 0, Integer.MAX_VALUE, I18n.translateToLocal("limitedlives.config.prop.banTime.comment"));
        healthAdjust = config.getInt("healthAdjust", cat, 1, 0, 1, I18n.translateToLocal("limitedlives.config.prop.healthAdjust.comment"));

        if(config.hasChanged())
        {
            config.save();
        }
    }
}
